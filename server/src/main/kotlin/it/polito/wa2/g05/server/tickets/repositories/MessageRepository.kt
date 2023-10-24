package it.polito.wa2.g05.server.tickets.repositories

import it.polito.wa2.g05.server.tickets.entities.Message
import it.polito.wa2.g05.server.tickets.entities.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import java.util.UUID

interface MessageRepository: JpaRepository<Message, UUID> {
    fun findAllByTicket(@Param("ticket") ticket: Ticket): List<Message>
}