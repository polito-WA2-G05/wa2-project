package it.polito.wa2.g05.server.tickets.services

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.authentication.security.JwtAuthConverter
import it.polito.wa2.g05.server.products.ProductNotFoundException
import it.polito.wa2.g05.server.products.repositories.ProductRepository
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.profiles.repositories.ProfileRepository
import it.polito.wa2.g05.server.tickets.*
import it.polito.wa2.g05.server.tickets.dtos.CreateTicketFormDTO
import it.polito.wa2.g05.server.tickets.dtos.StartTicketFormDTO
import it.polito.wa2.g05.server.tickets.dtos.TicketDTO
import it.polito.wa2.g05.server.tickets.dtos.toDTO
import it.polito.wa2.g05.server.tickets.entities.Change
import it.polito.wa2.g05.server.tickets.entities.Employee
import it.polito.wa2.g05.server.tickets.entities.Ticket
import it.polito.wa2.g05.server.tickets.repositories.ChangeRepository
import it.polito.wa2.g05.server.tickets.repositories.EmployeeRepository
import it.polito.wa2.g05.server.tickets.repositories.SpecializationRepository
import it.polito.wa2.g05.server.tickets.repositories.TicketRepository
import it.polito.wa2.g05.server.tickets.utils.PriorityLevel
import org.springframework.stereotype.Service
import it.polito.wa2.g05.server.tickets.utils.TicketStatus
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import java.util.*

@Service
@Observed
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val profileRepository: ProfileRepository,
    private val productRepository: ProductRepository,
    private val employeeRepository: EmployeeRepository,
    private val changeRepository: ChangeRepository,
    private val specializationRepository: SpecializationRepository,
    private val jwtAuthConverter: JwtAuthConverter
) : TicketService {

    private val log = LoggerFactory.getLogger("TicketServiceImpl")

    override fun createTicket(data: CreateTicketFormDTO, token: String): TicketDTO {
        val customerId = jwtAuthConverter.getUUID(token.replace("Bearer ", ""))
        val customer = profileRepository.findById(customerId)
            .orElseThrow {
                log.error("Profile with ${customerId} not found")
                ProfileNotFoundException("Profile ${customerId} not found")
            }

        val product = productRepository.findByEan(data.productEAN!!)
            .orElseThrow {
                log.error("Profile with ${data.productEAN} not found")
                ProductNotFoundException("Product with ${data.productEAN} not found")
            }

        val specialization = specializationRepository.findById(data.specializationId!!)
            .orElseThrow {
                log.error("Specialization ${data.specializationId} not found")
                SpecializationNotFoundException("Specialization ${data.specializationId} not found")
            }

        val ticket = ticketRepository.save(
            Ticket(
                TicketStatus.OPEN,
                data.title!!,
                data.description!!,
                customer,
                null,
                null,
                product,
                Date(),
                null,
                specialization
            )
        )

        changeRepository.save(Change(null, ticket.status, Date(), ticket, ticket.expert))
        return ticket.toDTO()
    }

    protected fun removeExpert(ticket: Ticket) {
        if (ticket.expert != null) {
            employeeRepository.decreaseIsWorkingOn(ticket.expert!!.id!!)
            ticketRepository.removeExpert(ticket.id!!)
        }
    }

    @Transactional
    override fun cancelTicket(id: Long, token: String): TicketDTO {
        val customerId = jwtAuthConverter.getUUID(token.replace("Bearer ", ""))

        val customer = profileRepository.findById(customerId)
            .orElseThrow {
                log.error("Profile $customerId not found")
                ProfileNotFoundException("Profile $customerId not found")
            }

        if (!ticketRepository.existsById(id)) {
            log.error("Ticket $id not found")
            throw TicketNotFoundException("Ticked $id not found")
        }

        if (ticketRepository.getCustomer(id) != customer) {
            log.error("You are not allowed to perform this action")
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus == TicketStatus.CANCELLED) {
            log.error("Status can't be set to CANCELLED from $currentStatus")
            throw TicketStatusNotValidException("Status can't be set to CANCELLED from $currentStatus")
        }

        ticketRepository.updateStatus(id, TicketStatus.CANCELLED, Date())

        val ticket = ticketRepository.findById(id).get()

        this.removeExpert(ticket)

        changeRepository.save(Change(currentStatus, TicketStatus.CANCELLED, Date(), ticket, ticket.expert))

        return ticket.toDTO()
    }

    @Transactional
    override fun expertCloseTicket(id: Long, token: String): TicketDTO {
        val expertId = jwtAuthConverter.getUUID(token.replace("Bearer ", ""))

        val expert = employeeRepository.findById(expertId)
            .orElseThrow {
                log.error("Expert $expertId not found")
                EmployeeNotFoundException("Expert $expertId not found")
            }

        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException("Ticked $id not found")
        }

        if (ticketRepository.getExpert(id) != expert) {
            log.error("You are not allowed to perform this action")
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus != TicketStatus.IN_PROGRESS) {
            log.error("Status can't be set to CLOSE from $currentStatus")
            throw TicketStatusNotValidException("Status can't be set to CLOSE from $currentStatus")
        }
        ticketRepository.updateStatus(id, TicketStatus.CLOSED, Date())
        val ticket = ticketRepository.findById(id).get()
        changeRepository.save(Change(currentStatus, TicketStatus.CLOSED, Date(), ticket, ticket.expert))

        return ticket.toDTO()
    }

    @Transactional
    override fun managerCloseTicket(id: Long): TicketDTO {
        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException("Ticked $id not found")
        }
        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus == TicketStatus.CLOSED || currentStatus == TicketStatus.CANCELLED || currentStatus == TicketStatus.IN_PROGRESS) {
            log.error("Status can't be set to CLOSE from $currentStatus")
            throw TicketStatusNotValidException("Status can't be set to CLOSE from $currentStatus")
        }

        ticketRepository.updateStatus(id, TicketStatus.CLOSED, Date())
        val ticket = ticketRepository.findById(id).get()
        changeRepository.save(Change(currentStatus, TicketStatus.CLOSED, Date(), ticket, ticket.expert))

        return ticket.toDTO()
    }

    @Transactional
    override fun reopenTicket(id: Long, token: String): TicketDTO {
        val customerId = jwtAuthConverter.getUUID(token.replace("Bearer ", ""))

        val customer = profileRepository.findById(customerId)
            .orElseThrow {
                log.error("Profile $customerId not found")
                ProfileNotFoundException("Profile $customerId not found")
            }

        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException("Ticked $id not found")
        }

        if (ticketRepository.getCustomer(id) != customer) {
            log.error("You are not allowed to perform this action")
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus == TicketStatus.RESOLVED || currentStatus == TicketStatus.CLOSED) {
            ticketRepository.updateStatus(id, TicketStatus.REOPENED)
            val ticket = ticketRepository.findById(id).get()
            this.removeExpert(ticket)
            changeRepository.save(
                Change(
                    currentStatus,
                    TicketStatus.REOPENED,
                    Date(),
                    ticket,
                    ticket.expert
                )
            )
            return ticket.toDTO()
        }

        throw TicketStatusNotValidException("Status can't be set to REOPENED from $currentStatus")
    }

    @Transactional
    override fun startTicket(id: Long, data: StartTicketFormDTO): TicketDTO {
        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus == TicketStatus.OPEN || currentStatus == TicketStatus.REOPENED) {
            if (ticketRepository.updateStatus(id, TicketStatus.IN_PROGRESS) == 0) {
                log.error("Ticket $id not found")
                throw TicketNotFoundException("Ticket $id not found")
            }

            val specialization = ticketRepository.getSpecialization(id)
            val employees = employeeRepository.findAllBySpecializations(specialization)

            var selectedEmployee: Employee = employees[0]
            var lowestIsWorkingOn = Int.MAX_VALUE

            for (employee in employees) {
                if (employee.workingOn < lowestIsWorkingOn) {
                    selectedEmployee = employee
                    lowestIsWorkingOn = employee.workingOn
                }
            }

            ticketRepository.startTicket(id, PriorityLevel.values()[data.priorityLevel!!], selectedEmployee)
            employeeRepository.increaseIsWorkingOn(selectedEmployee.id!!)

            val ticket = ticketRepository.findById(id).get()

            changeRepository.save(Change(currentStatus, TicketStatus.IN_PROGRESS, Date(), ticket, ticket.expert))

            return ticket.toDTO()
        }

        log.error("Status can't be set to IN_PROGRESS from $currentStatus")
        throw TicketStatusNotValidException("Status can't be set to IN_PROGRESS from $currentStatus")
    }

    @Transactional
    override fun stopTicket(id: Long, token: String): TicketDTO {
        val expertId = jwtAuthConverter.getUUID(token.replace("Bearer ", ""))

        val expert = employeeRepository.findById(expertId)
            .orElseThrow {
                log.error("Expert $expertId not found")
                EmployeeNotFoundException("Expert $expertId not found")
            }

        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException("Ticked $id not found")
        }
        if (ticketRepository.getExpert(id) != expert) {
            log.error("You are not allowed to perform this action")
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus == TicketStatus.IN_PROGRESS) {
            ticketRepository.updateStatus(id, TicketStatus.OPEN)
            val ticket = ticketRepository.findById(id).get()
            this.removeExpert(ticket)
            changeRepository.save(Change(currentStatus, TicketStatus.OPEN, Date(), ticket, ticket.expert))
            return ticket.toDTO()
        }

        log.error("Status can't be set to OPEN from $currentStatus")
        throw TicketStatusNotValidException("Status can't be set to OPEN from $currentStatus")
    }

    @Transactional
    override fun managerResolveTicket(id: Long): TicketDTO {

        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException("Ticked $id not found")
        }

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus == TicketStatus.OPEN || currentStatus == TicketStatus.REOPENED) {
            ticketRepository.updateStatus(id, TicketStatus.RESOLVED, Date())
            val ticket = ticketRepository.findById(id).get()

            this.removeExpert(ticket)
            changeRepository.save(
                Change(
                    currentStatus,
                    TicketStatus.RESOLVED,
                    Date(),
                    ticket,
                    ticket.expert
                )
            )
            return ticket.toDTO()
        }

        log.error("Status can't be set to RESOLVED from $currentStatus")
        throw TicketStatusNotValidException("Status can't be set to RESOLVED from $currentStatus")
    }

    @Transactional
    override fun expertResolveTicket(id: Long, token: String): TicketDTO {
        val expertId = jwtAuthConverter.getUUID(token.replace("Bearer ", ""))

        val expert = employeeRepository.findById(expertId)
            .orElseThrow {
                log.error("Expert $expertId not found")
                EmployeeNotFoundException("Expert $expertId not found")
            }

        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException("Ticked $id not found")
        }

        if (ticketRepository.getExpert(id) != expert) {
            log.error("You are not allowed to perform this action")
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus != TicketStatus.IN_PROGRESS) {
            log.error("Status can't be set to RESOLVED from $currentStatus")
            throw TicketStatusNotValidException("Status can't be set to RESOLVED from $currentStatus")
        }
        ticketRepository.updateStatus(id, TicketStatus.RESOLVED, Date())
        val ticket = ticketRepository.findById(id).get()

        this.removeExpert(ticket)
        changeRepository.save(
                Change(
                    currentStatus,
                    TicketStatus.RESOLVED,
                    Date(),
                    ticket,
                    ticket.expert
                )
        )
        return ticket.toDTO()
    }

    override fun getTicket(id: Long): TicketDTO =
        ticketRepository.findById(id).orElseThrow {
            log.error("Ticket $id not found")
            throw TicketNotFoundException("Ticket $id not found")
        }.toDTO()


    override fun getAllTicketsByProductId(productId: Long): List<TicketDTO> {
        val product = productRepository.findById(productId).orElseThrow {
            log.error("Product with id $productId not found")
            throw ProductNotFoundException("Product with id $productId not found")
        }

        return ticketRepository.findAllByProduct(product).map { it.toDTO() }
    }
}
