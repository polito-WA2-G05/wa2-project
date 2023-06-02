package it.polito.wa2.g05.server.tickets.services

import it.polito.wa2.g05.server.tickets.dtos.CreateTicketFormDTO
import it.polito.wa2.g05.server.tickets.dtos.StartTicketFormDTO
import it.polito.wa2.g05.server.tickets.dtos.TicketDTO

interface TicketService {
    fun createTicket(data: CreateTicketFormDTO, token: String): TicketDTO
    fun cancelTicket(id: Long, token:String): TicketDTO
    fun startTicket(id: Long, data: StartTicketFormDTO): TicketDTO
    fun stopTicket(id: Long, token: String): TicketDTO
    fun expertCloseTicket(id: Long, token: String): TicketDTO
    fun managerCloseTicket(id: Long): TicketDTO
    fun reopenTicket(id: Long, token: String): TicketDTO
    fun expertResolveTicket(id: Long, token: String): TicketDTO
    fun managerResolveTicket(id: Long): TicketDTO
    fun getTicket(id: Long): TicketDTO
    fun getAllTicketsByProductId(productEan: String): List<TicketDTO>
}