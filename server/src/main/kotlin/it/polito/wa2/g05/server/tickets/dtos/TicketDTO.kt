package it.polito.wa2.g05.server.tickets.dtos

import it.polito.wa2.g05.server.products.dtos.ProductDTO
import it.polito.wa2.g05.server.products.dtos.toDTO
import it.polito.wa2.g05.server.profiles.dtos.ProfileDTO
import it.polito.wa2.g05.server.profiles.dtos.toDTO
import it.polito.wa2.g05.server.tickets.entities.Ticket
import it.polito.wa2.g05.server.tickets.utils.PriorityLevel
import it.polito.wa2.g05.server.tickets.utils.TicketStatus
import java.util.Date

data class TicketDTO(
    val id : Long?,
    val status: TicketStatus?,
    val title: String,
    val description: String,
    val customer: ProfileDTO?,
    val expert: EmployeeDTO?,
    val priorityLevel: PriorityLevel?,
    val product: ProductDTO?,
    val createdDate: Date?,
    val closedDate: Date?,
    val specialization: SpecializationDTO?
)

fun Ticket.toDTO(): TicketDTO {
    return TicketDTO(getId(), status, title, description, customer?.toDTO(), expert?.toDTO(), priorityLevel , product?.toDTO(), createdDate, closedDate, specialization?.toDTO())
}
