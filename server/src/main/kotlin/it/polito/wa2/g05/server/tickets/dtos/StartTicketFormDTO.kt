package it.polito.wa2.g05.server.tickets.dtos

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.PositiveOrZero

data class StartTicketFormDTO(
    @field:PositiveOrZero
    @field:Max(3)
    var priorityLevel: Int,
)

