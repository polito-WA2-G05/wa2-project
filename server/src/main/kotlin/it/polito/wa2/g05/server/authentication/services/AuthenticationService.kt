package it.polito.wa2.g05.server.authentication.services

import it.polito.wa2.g05.server.authentication.dtos.CredentialsDTO
import org.springframework.http.HttpStatusCode

interface AuthenticationService {
    fun login(credentialDTO: CredentialsDTO): Any
    fun logout(token: String): HttpStatusCode

}