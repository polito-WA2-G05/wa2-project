package it.polito.wa2.g05.server.authentication.dtos

import jakarta.validation.constraints.NotEmpty

data class ExpertDetailsDTO(
    @field:NotEmpty
    val specializations: Set<Long>
) : UserDetailsDTO
