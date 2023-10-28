package it.polito.wa2.g05.server.tickets.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.tickets.dtos.MessageDTO
import it.polito.wa2.g05.server.tickets.dtos.SendMessageDTO
import it.polito.wa2.g05.server.tickets.services.MessageService
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.*

@Observed
@RestController
@RequestMapping
class MessageController(
    private val messageService: MessageService,
    private val template: SimpMessagingTemplate
) {
    private val log = LoggerFactory.getLogger("MessageController")

    /* GET /api/customer/tickets/{id}/messages/history */

    @GetMapping("/api/customer/tickets/{id}/messages/history")
    fun customerGetMessagesHistory(@RequestHeader("Authorization") token: String, @PathVariable id: Long) : List<MessageDTO> {
        log.info("Customer gets Ticket $id messages history")
        return messageService.customerGetMessagesHistory(token, id)
    }

    /* GET /api/expert/tickets/{id}/messages/history */

    @GetMapping("/api/expert/tickets/{id}/messages/history")
    fun expertGetMessagesHistory(@RequestHeader("Authorization") token: String, @PathVariable id: Long) : List<MessageDTO> {
        log.info("Expert gets Ticket $id messages history")
        return messageService.expertGetMessagesHistory(token, id)
    }

    // MESSAGE_SEND(WS) /app/messages

    @MessageMapping("/messages")
    fun receiveMessage(@Payload data: SendMessageDTO): MessageDTO {
        val receiver = messageService.saveMessage(data)

        // received by client subscribed to /queue/{user}/messages
        template.convertAndSendToUser(receiver, "/messages", data)
        return MessageDTO(data.text, data.timestamp, data.isFromCustomer)
    }
}