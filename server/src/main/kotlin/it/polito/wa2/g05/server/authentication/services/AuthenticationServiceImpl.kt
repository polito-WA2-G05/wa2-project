package it.polito.wa2.g05.server.authentication.services

import it.polito.wa2.g05.server.authentication.InvalidUserCredentialsException
import it.polito.wa2.g05.server.authentication.dtos.*
import it.polito.wa2.g05.server.authentication.security.JwtAuthConverter
import it.polito.wa2.g05.server.authentication.utils.Role
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.profiles.entities.Profile
import it.polito.wa2.g05.server.profiles.repositories.ProfileRepository
import it.polito.wa2.g05.server.tickets.EmployeeNotFoundException
import it.polito.wa2.g05.server.tickets.SpecializationNotFoundException
import it.polito.wa2.g05.server.tickets.entities.Employee
import it.polito.wa2.g05.server.tickets.repositories.EmployeeRepository
import it.polito.wa2.g05.server.tickets.repositories.SpecializationRepository
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler
import java.util.*

class CustomErrorHandler : ResponseErrorHandler {
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
    @Value("\${keycloak.realm}")
    private val realm: String,

    @Value("\${keycloak.resource}")
    private val resource: String,

    @Value("\${keycloak.credentials.secret}")
    private val secreteKey: String,

    @Value("\${keycloak.hostname}")
    private val keycloakHostname: String,

    private val keycloak: Keycloak,
    private val employeeRepository: EmployeeRepository,
    private val profileRepository: ProfileRepository,
    private val specializationRepository: SpecializationRepository,
    private val jwtAuthConverter: JwtAuthConverter
) : AuthenticationService {

    private fun preparePasswordRepresentation(password: String): CredentialRepresentation {
        val cR = CredentialRepresentation()
        cR.isTemporary = false
        cR.type = CredentialRepresentation.PASSWORD
        cR.value = password

        return cR
    }

    private fun prepareUserRepresentation(details: UserDetailsDTO , cR: CredentialRepresentation): UserRepresentation {
        val user = UserRepresentation()
        user.username = details.username
        user.credentials = listOf(cR)
        user.isEnabled = true
        user.email = details.email
        user.isEmailVerified = true

        return user
    }

    private fun findRoleByName(roleName: String): RoleRepresentation =
        keycloak.realm(realm).roles().get(roleName).toRepresentation()

    private fun assignRole(userId: String, roleRepresentation: RoleRepresentation) {
        keycloak
            .realm(realm)
            .users()
            .get(userId)
            .roles()
            .realmLevel()
            .add(listOf(roleRepresentation))
    }

    override fun signup(data: SignupProfileDTO) {
        val password = preparePasswordRepresentation(data.details!!.password!!)
        val user = prepareUserRepresentation(data.details, password)

        val response = keycloak.realm(realm).users().create(user)

        val userId = CreatedResponseUtil.getCreatedId(response)
        val role = this.findRoleByName(Role.CUSTOMER.realmRole)
        this.assignRole(userId, role)

        val profile = Profile(UUID.fromString(userId), data.name!!, data.surname!!, data.details!!.email!!)

        profileRepository.save(profile)
    }

    override fun createExpert(data: CreateExpertDTO) {
        val password = preparePasswordRepresentation(data.details!!.password!!)
        val user = prepareUserRepresentation(data.details, password)
        val response = keycloak.realm(realm).users().create(user)
        val userId = CreatedResponseUtil.getCreatedId(response)
        val role = this.findRoleByName(Role.EXPERT.realmRole)
        this.assignRole(userId, role)

        val specializations = data.specializations.map {
            specializationRepository.findById(it)
                .orElseThrow { SpecializationNotFoundException("Specialization with id $it not found") }
        }.toMutableSet()

        println(specializations.toString())

        val employee = Employee(UUID.fromString(userId), specializations)

        employeeRepository.save(employee)
    }



    override fun login(credential: CredentialsDTO): Any {
        val url = "http://${keycloakHostname}:8081/realms/wa2g05keycloak/protocol/openid-connect/token"

        val headers = HttpHeaders()
        headers.setBasicAuth(resource, secreteKey)
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val request = HttpEntity(
            "grant_type=password&client_id=wa2g05keycloak-client&username=${credential.username}&password=${credential.password}",
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

            val isEmployee= role == "Expert" || role == "Manager"

            val user = if(isEmployee) employeeRepository else profileRepository

            val exception = if(isEmployee) EmployeeNotFoundException("$role $uuid is not found")
                else ProfileNotFoundException("Customer $uuid is not found")

            user.findById(uuid).orElseThrow { exception }
            return UserDTO(
                accessToken,
                jwtAuthConverter.getEmail(accessToken),
                jwtAuthConverter.getUsername(accessToken),
            )
        } catch (e: InvalidUserCredentialsException) {
            throw e
        }
    }

    override fun logout(token: String): HttpStatusCode {
        val url = "http://${keycloakHostname}:8081/realms/wa2g05keycloak/protocol/openid-connect/logout"

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.set("Authorization", token)
        val request = HttpEntity<Unit>(headers)

        val restTemplate = RestTemplate()
        val response: ResponseEntity<String> = restTemplate.exchange(url, HttpMethod.GET, request, String::class.java)

        return response.statusCode
    }
}