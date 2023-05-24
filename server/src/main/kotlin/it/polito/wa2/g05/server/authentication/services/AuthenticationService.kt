package it.polito.wa2.g05.server.authentication.services

import it.polito.wa2.g05.server.authentication.dtos.*
import org.springframework.http.HttpStatusCode

interface AuthenticationService {
    fun login(credentialDTO: CredentialsDTO): UserDTO
    fun logout(token: String): HttpStatusCode
    fun signup(data: SignupProfileDTO): CreatedUserDTO
    fun createExpert(data: CreateExpertDTO): CreatedUserDTO
}