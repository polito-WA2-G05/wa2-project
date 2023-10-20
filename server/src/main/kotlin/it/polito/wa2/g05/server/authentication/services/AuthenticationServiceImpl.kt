package it.polito.wa2.g05.server.authentication.services

import it.polito.wa2.g05.server.authentication.dtos.*
import it.polito.wa2.g05.server.authentication.utils.UserDetails
import it.polito.wa2.g05.server.authentication.security.keycloak.KeycloakService
import it.polito.wa2.g05.server.authentication.utils.Role
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.profiles.dtos.toDTO
import it.polito.wa2.g05.server.profiles.entities.Profile
import it.polito.wa2.g05.server.profiles.repositories.ProfileRepository
import it.polito.wa2.g05.server.specializations.repositories.SpecializationRepository
import it.polito.wa2.g05.server.tickets.EmployeeNotFoundException
import it.polito.wa2.g05.server.tickets.SpecializationNotFoundException
import it.polito.wa2.g05.server.tickets.dtos.toDTO
import it.polito.wa2.g05.server.tickets.entities.Employee
import it.polito.wa2.g05.server.tickets.repositories.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.http.ResponseEntity

@Service
class AuthenticationServiceImpl(
    private val keycloakService: KeycloakService,
    private val employeeRepository: EmployeeRepository,
    private val profileRepository: ProfileRepository,
    private val specializationRepository: SpecializationRepository,
) : AuthenticationService {

    override fun signup(data: UserFormDTO<ProfileDetailsDTO>): CreatedUserDTO {
        val uuid = keycloakService.createUser(data.email, data.username, data.password, Role.CUSTOMER)

        val profile = Profile(uuid, data.details.name, data.details.surname, data.email)

        profileRepository.save(profile)

        return CreatedUserDTO(data.username, data.email)
    }

    override fun createExpert(data: UserFormDTO<ExpertDetailsDTO>): CreatedUserDTO {
        val specializations = data.details.specializations.map {
            specializationRepository.findById(it)
                .orElseThrow { SpecializationNotFoundException(it) }
        }.toMutableSet()

        val uuid = keycloakService.createUser(data.email, data.username, data.password, Role.EXPERT)
        val username = keycloakService.getUser(uuid).username

        val employee = Employee(uuid, username, specializations)

        employeeRepository.save(employee)

        return CreatedUserDTO(data.username, data.email)
    }

    private fun createAuthenticatedUser(isEmployee: Boolean, user: UserDetails): AuthenticatedUserDTO =
        AuthenticatedUserDTO(
            if (isEmployee) employeeRepository.findById(user.uuid).orElseThrow {
                EmployeeNotFoundException(user.uuid)
            }.toDTO()
            else profileRepository.findById(user.uuid).orElseThrow {
                ProfileNotFoundException(user.email)
            }.toDTO(),
            user
        )

    override fun login(credentials: CredentialsDTO): AuthenticatedUserDTO {
        val user = keycloakService.authenticateUser(credentials.username, credentials.password)

        val isEmployee = user.authorities.contains(Role.EXPERT.roleName)
                || user.authorities.contains(Role.MANAGER.roleName)

        return this.createAuthenticatedUser(isEmployee, user)
    }

    override fun logout(token: String): ResponseEntity<Unit> =
        keycloakService.invalidateToken(token)

    override fun refreshToken(data: RefreshTokenDTO): RefreshedTokensDTO {
        return keycloakService.refreshToken(data.refreshToken)
    }
}