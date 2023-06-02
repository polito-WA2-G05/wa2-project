package it.polito.wa2.g05.server.authentication.services

import it.polito.wa2.g05.server.authentication.dtos.*
import org.springframework.http.ResponseEntity

interface AuthenticationService {
    fun signup(data: UserFormDTO<ProfileDetailsDTO>): CreatedUserDTO
    fun createExpert(data: UserFormDTO<ExpertDetailsDTO>): CreatedUserDTO
    fun login(credentials: CredentialsDTO): AuthenticatedUserDTO
    fun logout(token: String): ResponseEntity<Unit>
    fun refreshToken(data: RefreshTokenDTO): RefreshedTokensDTO
}