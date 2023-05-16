package it.polito.wa2.g05.server.authentication.dtos

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.*

data class CredentialsDTO(
    @field:NotNull
    @field:NotBlank
    val username: String,

    @field:NotNull
    @field:NotBlank
    val password: String
)