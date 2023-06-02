package it.polito.wa2.g05.server.authentication.utils

import org.springframework.security.oauth2.jwt.Jwt
import java.util.UUID

data class UserDetails(private val jwt: Jwt, val refreshToken: String? = null) {
    val accessToken: String
    val uuid: UUID
    val email: String
    val username: String
    val authorities: MutableList<String> = mutableListOf()

    init {
        this.accessToken = this.jwt.tokenValue
        this.uuid = UUID.fromString(this.jwt.subject)
        this.email = this.jwt.getClaim("email")
        this.username = this.jwt.getClaim("preferred_username")
    }
}