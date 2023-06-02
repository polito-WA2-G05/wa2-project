package it.polito.wa2.g05.server.authentication.dtos

import jakarta.validation.constraints.NotBlank

data class CredentialsDTO(
    @field:NotBlank
    var username: String,

    @field:NotBlank
    var password: String
)