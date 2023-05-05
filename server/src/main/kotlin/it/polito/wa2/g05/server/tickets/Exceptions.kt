package it.polito.wa2.g05.server.tickets

class TicketNotFoundException(message: String): RuntimeException(message)
class EmployeeNotFoundException(message: String): RuntimeException(message)
class TicketStatusNotValidException(message: String): RuntimeException(message)
class SpecializationNotFoundException(message: String): RuntimeException(message)
