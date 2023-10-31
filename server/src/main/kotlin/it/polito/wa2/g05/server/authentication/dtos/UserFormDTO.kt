package it.polito.wa2.g05.server.authentication.dtos

import jakarta.validation.Valid
import jakarta.validation.constraints.*

data class UserFormDTO<T : UserDetailsDTO>(
    @field:NotBlank
    val username: String,

    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&.]{8,}$",
        message = "Invalid password format"
    )
    val password: String,

    @field:Valid
    val details: T
)
