package it.polito.wa2.g05.server.tickets.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CreateTicketFormDTO(
    @field:NotBlank
    var title: String,

    @field:NotBlank
    var description: String,

    @field:NotBlank
    var productEan: String,

    @field:Positive
    var specializationId: Long
)
