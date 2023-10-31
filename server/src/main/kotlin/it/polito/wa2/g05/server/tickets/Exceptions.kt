package it.polito.wa2.g05.server.tickets

import java.util.UUID

class TicketNotFoundException(id: Long) :
    RuntimeException("Ticket with id equals to $id not found")

class EmployeeNotFoundException(uuid: UUID) :
    RuntimeException("Employee with uuid equals to $uuid not found")

class SpecializationNotFoundException(id: Long) :
    RuntimeException("Specialization with id equals to $id not found")

class TicketStatusNotValidException(message: String) : RuntimeException(message)

class ForbiddenActionException(message: String) : RuntimeException(message)