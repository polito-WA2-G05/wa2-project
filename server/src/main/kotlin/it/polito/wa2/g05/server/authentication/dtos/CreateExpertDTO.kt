package it.polito.wa2.g05.server.authentication.dtos

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class CreateExpertDTO(
    @field:NotEmpty
    @field:NotNull
    val specializations: Set<Long>?,

    @field:NotNull
    @field:Valid
    val details: UserDetailsDTO?
)
