package it.polito.wa2.g05.server.notifications

import java.util.UUID

class NotificationNotFoundException(id: UUID) :
    RuntimeException("Notification with id equals to $id not found")