package it.polito.wa2.g05.server.tickets.dtos

import it.polito.wa2.g05.server.tickets.entities.Change
import it.polito.wa2.g05.server.tickets.utils.TicketStatus
import java.util.Date

data class ChangeDTO(
    val id: Long?,
    val fromStatus: TicketStatus?,
    val toStatus: TicketStatus,
    val timestamp: Date,
    val ticket: TicketDTO,
    val expert: EmployeeDTO?
)

fun Change.toDTO(): ChangeDTO {
    return ChangeDTO(id, fromStatus, toStatus , timestamp, ticket.toDTO(), expert?.toDTO())
}
