package it.polito.wa2.g05.server.tickets.dtos

import java.util.*

data class SendMessageDTO(
    val text: String,
    val timestamp: Date,
    val isFromCustomer: Boolean,
    val receiver: String,
    val ticket :Long,
    val sender: String
)
