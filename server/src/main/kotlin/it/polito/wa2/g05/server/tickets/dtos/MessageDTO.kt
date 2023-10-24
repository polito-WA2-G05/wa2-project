package it.polito.wa2.g05.server.tickets.dtos

import it.polito.wa2.g05.server.tickets.entities.Message
import java.util.Date

data class MessageDTO(
    val text: String,
    val timestamp: Date,
    val isFromCustomer: Boolean
)

fun Message.toDTO(): MessageDTO {
    return MessageDTO(text, timestamp,isFromCustomer)
}

