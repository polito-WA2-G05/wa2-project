package it.polito.wa2.g05.server.tickets.services

import it.polito.wa2.g05.server.tickets.dtos.CreateTicketFormDTO
import it.polito.wa2.g05.server.tickets.dtos.StartTicketFormDTO
import it.polito.wa2.g05.server.tickets.dtos.TicketDTO

interface TicketService {
    fun createTicket(data: CreateTicketFormDTO): TicketDTO
    fun cancelTicket(id: Long): TicketDTO
    fun startTicket(id: Long, data: StartTicketFormDTO): TicketDTO
    fun stopTicket(id: Long): TicketDTO
    fun expertCloseTicket(id: Long): TicketDTO
    fun managerCloseTicket(id: Long): TicketDTO
    fun reopenTicket(id: Long): TicketDTO
    fun expertResolveTicket(id: Long): TicketDTO

    fun managerResolveTicket(id: Long): TicketDTO
    fun getTicket(id: Long): TicketDTO
    fun getAllTicketsByProductId(productId: Long): List<TicketDTO>
}