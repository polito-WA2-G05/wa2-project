package it.polito.wa2.g05.server.authentication.dtos

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class SignupProfileDTO(
    @field:NotNull
    @field:NotBlank
    val name: String?,

    @field:NotNull
    @field:NotBlank
    val surname: String?,

    @field:NotNull
    @field:Valid
    val details: UserDetailsDTO?
)
