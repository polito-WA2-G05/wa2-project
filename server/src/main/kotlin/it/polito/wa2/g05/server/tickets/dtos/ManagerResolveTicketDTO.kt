package it.polito.wa2.g05.server.tickets.dtos

import jakarta.validation.constraints.NotBlank

data class ManagerResolveTicketDTO(
    @field:NotBlank
    var description: String,
)
