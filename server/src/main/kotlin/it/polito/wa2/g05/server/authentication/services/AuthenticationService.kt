package it.polito.wa2.g05.server.authentication.services

import it.polito.wa2.g05.server.authentication.dtos.CreateExpertDTO
import it.polito.wa2.g05.server.authentication.dtos.CredentialsDTO
import it.polito.wa2.g05.server.authentication.dtos.SignupProfileDTO
import org.springframework.http.HttpStatusCode

interface AuthenticationService {
    fun login(credentialDTO: CredentialsDTO): Any
    fun logout(token: String): HttpStatusCode
    fun signup(data: SignupProfileDTO)
    fun createExpert(data: CreateExpertDTO)
}