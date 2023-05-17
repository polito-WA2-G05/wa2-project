package it.polito.wa2.g05.server.authentication.services

import it.polito.wa2.g05.server.authentication.InvalidUserCredentialsException
import it.polito.wa2.g05.server.authentication.dtos.CredentialsDTO
import it.polito.wa2.g05.server.authentication.dtos.UserDTO
import it.polito.wa2.g05.server.authentication.security.JwtAuthConverter
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.profiles.repositories.ProfileRepository
import it.polito.wa2.g05.server.tickets.EmployeeNotFoundException
import it.polito.wa2.g05.server.tickets.repositories.EmployeeRepository
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler

class CustomErrorHandler: ResponseErrorHandler {
    override fun hasError(response: ClientHttpResponse): Boolean {
        return response.statusCode.isError
    }

    override fun handleError(response: ClientHttpResponse) {
        if (response.statusCode == HttpStatus.UNAUTHORIZED)
            throw InvalidUserCredentialsException("Invalid credentials")
    }

}

@Service
class AuthenticationServiceImpl(
    val employeeRepository: EmployeeRepository,
    val profileRepository: ProfileRepository,
    val jwtAuthConverter: JwtAuthConverter
) : AuthenticationService {
    override fun login(credentialDTO: CredentialsDTO): Any {
        val url = "http://localhost:8081/realms/wa2g05keycloak/protocol/openid-connect/token"

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val request = HttpEntity(
            "grant_type=password&client_id=wa2g05keycloak-client&username=${credentialDTO.username}&password=${credentialDTO.password}",
            headers
        )

        val restTemplate = RestTemplateBuilder().errorHandler(CustomErrorHandler()).build()

        try {
            val response: ResponseEntity<Map<String, Any>> = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                object : ParameterizedTypeReference<Map<String, Any>>() {})

            val accessToken = response.body?.get("access_token") as String

            val role = jwtAuthConverter.getRole(accessToken)
            val uuid = jwtAuthConverter.getUUID(accessToken)

            if (role == "Expert" || role == "Manager") {
                val employee = employeeRepository.findById(uuid).orElseThrow { EmployeeNotFoundException("$role $uuid is not found") }

                return UserDTO(
                        accessToken,
                        jwtAuthConverter.getEmail(accessToken),
                        jwtAuthConverter.getUsername(accessToken),
                        null,
                        null,
                        if (role == "Expert") employee.workingOn else null
                )
            } else {
                val profile = profileRepository.findById(uuid).orElseThrow { ProfileNotFoundException("Customer $uuid is not found") }
                return UserDTO(
                        accessToken,
                        jwtAuthConverter.getEmail(accessToken),
                        jwtAuthConverter.getUsername(accessToken),
                        profile.name,
                        profile.surname,
                        null
                )
            }
        } catch (e: InvalidUserCredentialsException){
            throw e
        }
    }

    override fun logout(token: String): HttpStatusCode {
        val url = "http://localhost:8081/realms/wa2g05keycloak/protocol/openid-connect/logout"

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.set("Authorization", token)
        val request = HttpEntity<Unit>(headers)

        val restTemplate = RestTemplate()
        val response: ResponseEntity<String> = restTemplate.exchange(url, HttpMethod.GET, request, String::class.java)

        return response.statusCode
    }
}