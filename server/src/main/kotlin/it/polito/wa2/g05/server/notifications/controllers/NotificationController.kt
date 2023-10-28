package it.polito.wa2.g05.server.notifications.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.notifications.dtos.ManagersNotificationDTO
import it.polito.wa2.g05.server.notifications.dtos.NotificationDTO
import it.polito.wa2.g05.server.notifications.dtos.SendNotificationDTO
import it.polito.wa2.g05.server.notifications.services.NotificationService
import it.polito.wa2.g05.server.tickets.dtos.MessageDTO
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.*

@Observed
@RestController
@RequestMapping
class NotificationController(
    private val template: SimpMessagingTemplate,
    private val notificationService: NotificationService
) {
    private val log = LoggerFactory.getLogger("NotificationController")

    @GetMapping("/api/authenticated/notifications")
    fun getNotificationsHistory(@RequestHeader("Authorization") token: String): List<NotificationDTO> {
        log.info("User gets notifications history")
        return notificationService.getNotificationsHistory(token)
    }

    @DeleteMapping("/api/authenticated/notifications/{id}")
    fun deleteNotification(@RequestHeader("Authorization") token: String, @PathVariable("id") id: String): List<NotificationDTO> {
        log.info("User delete the notification with id = $id")
        return notificationService.deleteNotification(token, id)
    }

    // MESSAGE_SEND(WS) /app/notifications

    @MessageMapping("/notifications")
    fun receiveNotification(@Payload data: SendNotificationDTO) {
        val receiver = data.receiver
        val notification = notificationService.saveNotification(data)
        template.convertAndSendToUser(receiver, "/notifications", notification)
        return
    }

    // MESSAGE_SEND(WS) /app/notifications/managers

    @MessageMapping("/notifications/managers")
    fun managersReceiveNotification(@Payload data: ManagersNotificationDTO) {
        notificationService.getManagerIdentifiers().forEach {
            val sendNotificationDTO = SendNotificationDTO(data.text, it, data.timestamp)
            val notification = notificationService.saveNotification(sendNotificationDTO)
            template.convertAndSendToUser(it, "/notifications", notification)
        }

        return
    }
}
