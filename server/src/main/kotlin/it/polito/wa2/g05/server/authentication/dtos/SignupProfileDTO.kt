package it.polito.wa2.g05.server.authentication.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SignupProfileDTO(
    @field:NotNull
    @field:NotBlank
    val name: String?,

    @field:NotNull
    @field:NotBlank
    val surname: String?,

    @field:NotNull
    val details: UserDetailsDTO?
)
