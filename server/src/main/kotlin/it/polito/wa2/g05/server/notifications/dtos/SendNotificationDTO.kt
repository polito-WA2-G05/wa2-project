package it.polito.wa2.g05.server.notifications.dtos

import java.util.*

data class SendNotificationDTO(
    var text: String,
    var receiver: String,
    var timestamp: Date
)
