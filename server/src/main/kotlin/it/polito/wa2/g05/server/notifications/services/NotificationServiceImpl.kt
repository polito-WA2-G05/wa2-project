package it.polito.wa2.g05.server.notifications.services

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.authentication.security.jwt.JwtDecoder
import it.polito.wa2.g05.server.authentication.security.keycloak.KeycloakService
import it.polito.wa2.g05.server.authentication.utils.Role
import it.polito.wa2.g05.server.authentication.utils.UserDetails
import it.polito.wa2.g05.server.notifications.NotificationNotFoundException
import it.polito.wa2.g05.server.notifications.dtos.NotificationDTO
import it.polito.wa2.g05.server.notifications.dtos.SendNotificationDTO
import it.polito.wa2.g05.server.notifications.dtos.toDTO
import it.polito.wa2.g05.server.notifications.entities.Notification
import it.polito.wa2.g05.server.notifications.repositories.NotificationRepository
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.profiles.repositories.ProfileRepository
import it.polito.wa2.g05.server.tickets.EmployeeNotFoundException
import it.polito.wa2.g05.server.tickets.ForbiddenActionException
import it.polito.wa2.g05.server.tickets.repositories.EmployeeRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Observed
@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
    private val profileRepository: ProfileRepository,
    private val employeeRepository: EmployeeRepository,
    private val keycloakService: KeycloakService,
    private val jwtDecoder: JwtDecoder
): NotificationService {

    private val log = LoggerFactory.getLogger("NotificationServiceImpl")

    override fun getNotificationsHistory(token: String): List<NotificationDTO> {
        val userId = UserDetails(jwtDecoder.decode(token)).uuid
        return notificationRepository.findAllByReceiver(userId).map { it.toDTO() }
            .sortedByDescending { it.timestamp }
    }

    @Transactional
    override fun saveNotification(data: SendNotificationDTO): NotificationDTO {
        val auhthorities = keycloakService.getUserAuthorities(data.receiver)

        if (auhthorities.contains(Role.CUSTOMER.roleName)) {
            if (!profileRepository.existsById(UUID.fromString(data.receiver))) {
                log.error("Receiver profile not found")
                throw ProfileNotFoundException(data.receiver)
            }
        } else {
            if (!employeeRepository.existsById(UUID.fromString(data.receiver))) {
                log.error("Receiver employee not found")
                throw EmployeeNotFoundException(UUID.fromString(data.receiver))
            }
        }

        return notificationRepository.save(Notification(
            data.text,
            UUID.fromString(data.receiver),
            data.timestamp
        )).toDTO()
    }

    override fun deleteNotification(token: String, id: String): List<NotificationDTO> {
        val userId = UserDetails(jwtDecoder.decode(token)).uuid

        val notification = notificationRepository.findById(UUID.fromString(id)).orElseThrow {
            log.error("Notification with id = $id not found")
            throw NotificationNotFoundException(UUID.fromString(id))
        }

        if (notification.receiver != userId) {
            log.error("User is not the receiver. He is not allowed to delete this notification")
            throw ForbiddenActionException("You are not allowed to delete notification with id $id")
        }

        notificationRepository.delete(notification)

        return notificationRepository.findAllByReceiver(userId).map { it.toDTO() }
    }

    override fun getManagerIdentifiers(): List<String> =
        employeeRepository.findAll().filter {
            keycloakService.getUserAuthorities(it.id.toString())
                .contains(Role.MANAGER.roleName)
        }.map { it.id.toString() }
}