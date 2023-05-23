package it.polito.wa2.g05.server.authentication.dtos

import jakarta.validation.constraints.*

data class UserDetailsDTO(
    @field:NotNull
    @field:NotBlank
    val username: String?,

    @field:Email
    @field:NotNull
    @field:NotBlank
    val email: String?,

    @field:NotNull
    @field:NotBlank
    @field:Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Invalid password format")
    val password: String?
)
