package it.polito.wa2.g05.server.authentication.dtos

import jakarta.validation.constraints.NotBlank

data class RefreshTokenDTO(
    @field:NotBlank
    val refreshToken: String
)
