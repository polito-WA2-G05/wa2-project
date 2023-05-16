package it.polito.wa2.g05.server.tickets.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.util.UUID

data class CreateTicketFormDTO(
    @field:NotNull
    @field:NotBlank
    var title: String?,

    @field:NotNull
    @field:NotBlank
    var description: String?,

    @field:Positive
    var customerId: UUID?,

    @field:NotNull
    @field:NotBlank
    var productEAN: String?,

    @field:Positive
    @field:NotNull
    var specializationId: Long?
)
