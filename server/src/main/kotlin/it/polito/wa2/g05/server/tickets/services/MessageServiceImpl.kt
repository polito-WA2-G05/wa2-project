package it.polito.wa2.g05.server.tickets.services

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.authentication.security.jwt.JwtDecoder
import it.polito.wa2.g05.server.authentication.utils.UserDetails
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.profiles.entities.Profile
import it.polito.wa2.g05.server.profiles.repositories.ProfileRepository
import it.polito.wa2.g05.server.tickets.EmployeeNotFoundException
import it.polito.wa2.g05.server.tickets.ForbiddenActionException
import it.polito.wa2.g05.server.tickets.TicketNotFoundException
import it.polito.wa2.g05.server.tickets.TicketStatusNotValidException
import it.polito.wa2.g05.server.tickets.dtos.MessageDTO
import it.polito.wa2.g05.server.tickets.dtos.SendMessageDTO
import it.polito.wa2.g05.server.tickets.dtos.toDTO
import it.polito.wa2.g05.server.tickets.entities.Employee
import it.polito.wa2.g05.server.tickets.entities.Message
import it.polito.wa2.g05.server.tickets.repositories.EmployeeRepository
import it.polito.wa2.g05.server.tickets.repositories.MessageRepository
import it.polito.wa2.g05.server.tickets.repositories.TicketRepository
import it.polito.wa2.g05.server.tickets.utils.TicketStatus
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.util.*

@Observed
@Service
class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val profileRepository: ProfileRepository,
    private val ticketRepository: TicketRepository,
    private val employeeRepository: EmployeeRepository,
    private val jwtDecoder: JwtDecoder
) : MessageService {

    private val log = LoggerFactory.getLogger("MessageServiceImpl")

    override fun customerGetMessagesHistory(token: String, id: Long): List<MessageDTO> {
        val customerId = UserDetails(jwtDecoder.decode(token)).uuid

        val customer = profileRepository.findById(customerId)
            .orElseThrow {
                log.error("Profile $customerId not found")
                ProfileNotFoundException(customerId.toString())
            }

        if (!ticketRepository.existsById(id)) {
            log.error("Ticket $id not found")
            throw TicketNotFoundException(id)
        }

        if (ticketRepository.getCustomer(id) != customer) {
            log.error("You are not allowed to perform this action")
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        val ticket = ticketRepository.findById(id).get()
        return messageRepository.findAllByTicket(ticket).map { it.toDTO() }
    }

    override fun expertGetMessagesHistory(token: String, id: Long): List<MessageDTO> {
        val expertId = UserDetails(jwtDecoder.decode(token)).uuid

        val expert = employeeRepository.findById(expertId)
            .orElseThrow {
                log.error("Expert $expertId not found")
                EmployeeNotFoundException(expertId)
            }

        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException(id)
        }

        if (ticketRepository.getExpert(id) != expert) {
            log.error("You are not allowed to perform this action")
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        val ticket = ticketRepository.findById(id).get()
        return messageRepository.findAllByTicket(ticket).map { it.toDTO() }
    }

    override fun saveMessage(data: SendMessageDTO): String {
        val ticket = ticketRepository.findById(data.ticket).orElseThrow {
            log.error("Ticked ${data.ticket} not found")
            throw TicketNotFoundException(data.ticket)
        }

        if (ticket.status != TicketStatus.IN_PROGRESS) {
            log.error("Messages can not be sent if ticket is not IN_PROGRESS")
            throw TicketStatusNotValidException("Messages can not be sent if ticket is not IN_PROGRESS")
        }

        val customerId: UUID
        val expertId: UUID

        if (data.isFromCustomer) {
            customerId = UUID.fromString(data.sender)
            expertId = UUID.fromString(data.receiver)
        } else {
            customerId = UUID.fromString(data.receiver)
            expertId = UUID.fromString(data.sender)
        }

        val customer = profileRepository.findById(customerId).orElseThrow {
            log.error("Profile $customerId not found")
            ProfileNotFoundException(customerId.toString())
        }

        val expert = employeeRepository.findById(expertId).orElseThrow {
            log.error("Expert $expertId not found")
            EmployeeNotFoundException(expertId)
        }

        if (ticket.customer != customer || ticket.expert != expert) {
            log.error("Sender or receiver not associated with ticket ${ticket.id}")
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        messageRepository.save(Message(
            data.text,
            data.timestamp,
            ticket,
            data.isFromCustomer)
        )

        return data.receiver
    }
}