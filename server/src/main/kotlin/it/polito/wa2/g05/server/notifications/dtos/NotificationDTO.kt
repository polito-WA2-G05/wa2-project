package it.polito.wa2.g05.server.notifications.dtos

import it.polito.wa2.g05.server.notifications.entities.Notification
import java.util.*

data class NotificationDTO(
    var id: String,
    var text: String,
    var timestamp: Date
)

fun Notification.toDTO(): NotificationDTO {
    return NotificationDTO(id.toString(), text, timestamp)
}