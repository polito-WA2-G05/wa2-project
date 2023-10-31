package it.polito.wa2.g05.server.tickets.services

import it.polito.wa2.g05.server.tickets.dtos.MessageDTO
import it.polito.wa2.g05.server.tickets.dtos.SendMessageDTO
import org.springframework.core.io.Resource


interface MessageService {
    fun customerGetMessagesHistory(token: String, id: Long): List<MessageDTO>
    fun expertGetMessagesHistory(token: String, id: Long): List<MessageDTO>
    fun saveMessage(data: SendMessageDTO): String
}