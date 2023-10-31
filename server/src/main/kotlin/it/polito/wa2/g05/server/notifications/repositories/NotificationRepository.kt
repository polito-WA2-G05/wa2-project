package it.polito.wa2.g05.server.notifications.repositories

import it.polito.wa2.g05.server.notifications.entities.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import java.util.UUID

interface NotificationRepository: JpaRepository<Notification, UUID> {
    fun findAllByReceiver(@Param("receiver") receiver: UUID): List<Notification>
}