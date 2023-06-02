package it.polito.wa2.g05.server.authentication.dtos

import jakarta.validation.constraints.NotBlank

data class ProfileDetailsDTO(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    val surname: String
) : UserDetailsDTO
