package it.polito.wa2.g05.server.tickets.utils

enum class TicketStatus(val status: String) {
        OPEN("OPEN"),
        CLOSED("CLOSED"),
        CANCELLED("CANCELLED"),
        IN_PROGRESS("IN_PROGRESS"),
        RESOLVED("RESOLVED"),
        REOPENED("REOPENED")
}