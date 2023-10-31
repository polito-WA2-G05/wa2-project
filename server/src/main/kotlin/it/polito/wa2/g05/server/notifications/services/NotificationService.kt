package it.polito.wa2.g05.server.notifications.services

import it.polito.wa2.g05.server.notifications.dtos.NotificationDTO
import it.polito.wa2.g05.server.notifications.dtos.SendNotificationDTO

interface NotificationService {
    fun getNotificationsHistory(token: String): List<NotificationDTO>
    fun saveNotification(data: SendNotificationDTO): NotificationDTO
    fun deleteNotification(token: String, id: String): List<NotificationDTO>

    fun getManagerIdentifiers(): List<String>
}