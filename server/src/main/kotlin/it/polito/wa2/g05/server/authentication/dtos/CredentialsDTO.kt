package it.polito.wa2.g05.server.authentication.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CredentialsDTO(
    @field:NotNull
    @field:NotBlank
    var username: String?,

    @field:NotNull
    @field:NotBlank
    var password: String?
)