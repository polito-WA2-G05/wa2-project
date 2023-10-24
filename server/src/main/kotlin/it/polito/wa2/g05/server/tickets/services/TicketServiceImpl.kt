package it.polito.wa2.g05.server.tickets.services

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.authentication.dtos.ExpertDetailsDTO
import it.polito.wa2.g05.server.authentication.security.jwt.JwtDecoder
import it.polito.wa2.g05.server.authentication.security.keycloak.KeycloakService
import it.polito.wa2.g05.server.authentication.utils.Role
import it.polito.wa2.g05.server.authentication.utils.UserDetails
import it.polito.wa2.g05.server.products.ProductNotFoundException
import it.polito.wa2.g05.server.products.repositories.ProductRepository
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.profiles.repositories.ProfileRepository
import it.polito.wa2.g05.server.purchases.PurchaseNotFoundException
import it.polito.wa2.g05.server.purchases.repository.PurchaseRepository
import it.polito.wa2.g05.server.tickets.*
import it.polito.wa2.g05.server.tickets.entities.Change
import it.polito.wa2.g05.server.tickets.entities.Employee
import it.polito.wa2.g05.server.tickets.entities.Ticket
import it.polito.wa2.g05.server.tickets.repositories.ChangeRepository
import it.polito.wa2.g05.server.tickets.repositories.EmployeeRepository
import it.polito.wa2.g05.server.specializations.repositories.SpecializationRepository
import it.polito.wa2.g05.server.tickets.dtos.*
import it.polito.wa2.g05.server.tickets.entities.Survey
import it.polito.wa2.g05.server.tickets.repositories.SurveyRepository
import it.polito.wa2.g05.server.tickets.repositories.TicketRepository
import it.polito.wa2.g05.server.tickets.utils.PriorityLevel
import it.polito.wa2.g05.server.tickets.utils.Rating
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
    private val purchaseRepository: PurchaseRepository,
    private val employeeRepository: EmployeeRepository,
    private val changeRepository: ChangeRepository,
    private val specializationRepository: SpecializationRepository,
    private val surveyRepository: SurveyRepository,
    private val keycloakService: KeycloakService,
    private val jwtDecoder: JwtDecoder
) : TicketService {

    private val log = LoggerFactory.getLogger("TicketServiceImpl")

    @Transactional
    override fun createTicket(data: CreateTicketFormDTO, token: String): TicketDTO {
        val customerId = UserDetails(jwtDecoder.decode(token)).uuid
        val customer = profileRepository.findById(customerId)
            .orElseThrow {
                log.error("Profile with ${customerId} not found")
                ProfileNotFoundException(customerId.toString())
            }

        val product = productRepository.findByEan(data.productEan)
            .orElseThrow {
                log.error("Product with ${data.productEan} not found")
                ProductNotFoundException(data.productEan)
            }

        if (!purchaseRepository.findAllByProfile(customer).flatMap { it.products }.contains(product)) {
            throw ForbiddenActionException("You have not purchased product on which you want to open the ticket")
        }

        val specialization = specializationRepository.findById(data.specializationId)
            .orElseThrow {
                log.error("Specialization ${data.specializationId} not found")
                SpecializationNotFoundException(data.specializationId)
            }

        val ticket = ticketRepository.save(
            Ticket(
                TicketStatus.OPEN,
                data.title,
                data.description,
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

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus == TicketStatus.CANCELLED) {
            log.error("Status can't be set to CANCELLED from $currentStatus")
            throw TicketStatusNotValidException("Status can't be set to CANCELLED from $currentStatus")
        }

        ticketRepository.updateStatus(id, TicketStatus.CANCELLED, Date())

        val ticket = ticketRepository.findById(id).get()

        changeRepository.save(Change(currentStatus, TicketStatus.CANCELLED, Date(), ticket, ticket.expert))
        this.removeExpert(ticket)



        return ticket.toDTO()
    }

    @Transactional
    override fun expertCloseTicket(id: Long, token: String): TicketDTO {
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

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus != TicketStatus.IN_PROGRESS) {
            log.error("Status can't be set to CLOSE from $currentStatus")
            throw TicketStatusNotValidException("Status can't be set to CLOSE from $currentStatus")
        }
        ticketRepository.updateStatus(id, TicketStatus.CLOSED, Date())
        val ticket = ticketRepository.findById(id).get()
        changeRepository.save(Change(currentStatus, TicketStatus.CLOSED, Date(), ticket, ticket.expert))
        this.removeExpert(ticket)

        return ticket.toDTO()
    }

    @Transactional
    override fun managerCloseTicket(id: Long): TicketDTO {
        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException(id)
        }
        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus == TicketStatus.CLOSED || currentStatus == TicketStatus.CANCELLED || currentStatus == TicketStatus.IN_PROGRESS) {
            log.error("Status can't be set to CLOSE from $currentStatus")
            throw TicketStatusNotValidException("Status can't be set to CLOSE from $currentStatus")
        }

        if (currentStatus == TicketStatus.RESOLVED && ticketRepository.getSurvey(id) == null) {
            log.error("Ticket can not be CLOSED if survey has not been sent by the customer yet")
            throw TicketStatusNotValidException("Ticket can not be CLOSED if survey has not been sent by the customer yet")
        }

        ticketRepository.updateStatus(id, TicketStatus.CLOSED, Date())

        val ticket = ticketRepository.findById(id).get()

        changeRepository.save(Change(currentStatus, TicketStatus.CLOSED, Date(), ticket, ticket.expert))
        this.removeExpert(ticket)

        return ticket.toDTO()
    }

    @Transactional
    override fun reopenTicket(id: Long, token: String): TicketDTO {
        val customerId = UserDetails(jwtDecoder.decode(token)).uuid

        val customer = profileRepository.findById(customerId)
            .orElseThrow {
                log.error("Profile $customerId not found")
                ProfileNotFoundException(customerId.toString())
            }

        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException(id)
        }

        if (ticketRepository.getCustomer(id) != customer) {
            log.error("You are not allowed to perform this action")
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus == TicketStatus.RESOLVED || currentStatus == TicketStatus.CLOSED) {
            ticketRepository.updateStatus(id, TicketStatus.REOPENED)

            ticketRepository.removeResolvedDescription(id)

            val survey = ticketRepository.getSurvey(id)
            if (survey != null) {
                ticketRepository.removeSurvey(id)
                surveyRepository.delete(survey)
            }

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
        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException(id)
        }

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus == TicketStatus.OPEN || currentStatus == TicketStatus.REOPENED) {
            if (ticketRepository.updateStatus(id, TicketStatus.IN_PROGRESS) == 0) {
                log.error("Ticket $id not found")
                throw TicketNotFoundException(id)
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

            ticketRepository.startTicket(id, PriorityLevel.values()[data.priorityLevel], selectedEmployee)
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

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus == TicketStatus.IN_PROGRESS) {
            ticketRepository.updateStatus(id, TicketStatus.OPEN)
            val ticket = ticketRepository.findById(id).get()
            changeRepository.save(Change(currentStatus, TicketStatus.OPEN, Date(), ticket, ticket.expert))
            this.removeExpert(ticket)
            return ticket.toDTO()
        }

        log.error("Status can't be set to OPEN from $currentStatus")
        throw TicketStatusNotValidException("Status can't be set to OPEN from $currentStatus")
    }

    @Transactional
    override fun managerResolveTicket(id: Long, data: ManagerResolveTicketDTO): TicketDTO {

        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException(id)
        }

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus == TicketStatus.OPEN || currentStatus == TicketStatus.REOPENED) {
            ticketRepository.updateStatus(id, TicketStatus.RESOLVED, Date())
            ticketRepository.setResolvedDescription(id, data.description)
            val ticket = ticketRepository.findById(id).get()

            changeRepository.save(
                Change(
                    currentStatus,
                    TicketStatus.RESOLVED,
                    Date(),
                    ticket,
                    ticket.expert
                )
            )
            this.removeExpert(ticket)
            return ticket.toDTO()
        }

        log.error("Status can't be set to RESOLVED from $currentStatus")
        throw TicketStatusNotValidException("Status can't be set to RESOLVED from $currentStatus")
    }

    @Transactional
    override fun expertResolveTicket(id: Long, token: String): TicketDTO {
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

        val currentStatus = ticketRepository.getStatus(id)

        if (currentStatus != TicketStatus.IN_PROGRESS) {
            log.error("Status can't be set to RESOLVED from $currentStatus")
            throw TicketStatusNotValidException("Status can't be set to RESOLVED from $currentStatus")
        }

        ticketRepository.updateStatus(id, TicketStatus.RESOLVED, Date())
        val ticket = ticketRepository.findById(id).get()

        changeRepository.save(
            Change(
                currentStatus,
                TicketStatus.RESOLVED,
                Date(),
                ticket,
                ticket.expert
            )
        )
        this.removeExpert(ticket)
        return ticket.toDTO()
    }

    override fun customerGetTicket(id: Long, token: String): TicketDTO {
        val customerId = UserDetails(jwtDecoder.decode(token)).uuid

        val customer = profileRepository.findById(customerId)
            .orElseThrow {
                log.error("Profile $customerId not found")
                ProfileNotFoundException(customerId.toString())
            }

        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException(id)
        }

        if (ticketRepository.getCustomer(id) != customer) {
            log.error("You are not allowed to perform this action")
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        return ticketRepository.findById(id).get().toDTO()
    }

    override fun expertGetTicket(id: Long, token: String): TicketDTO {
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

        return ticketRepository.findById(id).get().toDTO()
    }

    override fun managerGetTicket(id: Long): TicketDTO =
        ticketRepository.findById(id).orElseThrow {
            log.error("Ticket $id not found")
            throw TicketNotFoundException(id)
        }.toDTO()


    override fun managerGetTickets(productEan: String?): List<TicketDTO> {
        return if (productEan.isNullOrBlank())
            ticketRepository.findAll().map { it.toDTO() }
        else {
            val product = productRepository.findByEan(productEan).orElseThrow {
                log.error("Product with ean $productEan not found")
                throw ProductNotFoundException(productEan)
            }

            ticketRepository.findAllByProduct(product).map { it.toDTO() }
        }
    }

    override fun expertGetTickets(token: String, productEan: String?): List<TicketDTO> {
        val expertId = UserDetails(jwtDecoder.decode(token)).uuid

        val expert = employeeRepository.findById(expertId)
            .orElseThrow {
                log.error("Expert $expertId not found")
                EmployeeNotFoundException(expertId)
            }

        return if (productEan.isNullOrBlank())
            ticketRepository.findAllByExpert(expert).map { it.toDTO() }
        else {
            val product = productRepository.findByEan(productEan).orElseThrow {
                log.error("Product with ean $productEan not found")
                throw ProductNotFoundException(productEan)
            }

            ticketRepository.findAllByProductAndExpert(product, expert).map { it.toDTO() }
        }
    }

    override fun customerGetTickets(token: String, productEan: String?): List<TicketDTO> {
        val customerId = UserDetails(jwtDecoder.decode(token)).uuid

        val customer = profileRepository.findById(customerId)
            .orElseThrow {
                log.error("Profile $customerId not found")
                ProfileNotFoundException(customerId.toString())
            }

        return if (productEan.isNullOrBlank())
            ticketRepository.findAllByCustomer(customer).map { it.toDTO() }
        else {
            val product = productRepository.findByEan(productEan).orElseThrow {
                log.error("Product with ean $productEan not found")
                throw ProductNotFoundException(productEan)
            }

            ticketRepository.findAllByProductAndCustomer(product, customer).map { it.toDTO() }
        }
    }

    override fun getChanges(): List<ChangeDTO> =
        changeRepository.findAll().map { it.toDTO() }

    override fun getExperts(): List<EmployeeDTO> =
        employeeRepository.findAll().filter {
            keycloakService.getUserAuthorities(it.id.toString())
                .contains(Role.EXPERT.roleName)
        }.map { it.toDTO() }

    @Transactional
    override fun createSurvey(token: String, data: CreateSurveyDTO, id: Long): TicketDTO {
        val customerId = UserDetails(jwtDecoder.decode(token)).uuid

        val customer = profileRepository.findById(customerId)
            .orElseThrow {
                log.error("Profile $customerId not found")
                ProfileNotFoundException(customerId.toString())
            }

        if (!ticketRepository.existsById(id)) {
            log.error("Ticked $id not found")
            throw TicketNotFoundException(id)
        }

        if (ticketRepository.getCustomer(id) != customer) {
            log.error("You are not allowed to perform this action")
            throw ForbiddenActionException("You are not allowed to perform this action")
        }

        if (ticketRepository.getStatus(id) != TicketStatus.RESOLVED) {
            log.error("Survey can not be sent if the ticket is not RESOLVED")
            throw TicketStatusNotValidException("Survey can not be sent if the ticket is not RESOLVED")
        }

        val survey = surveyRepository.save(Survey(
            Rating.values()[data.serviceValuation],
            Rating.values()[data.professionality],
            data.comment,
            ticketRepository.findById(id).get()
        ))

        ticketRepository.saveSurvey(id, survey)

        return ticketRepository.findById(id).get().toDTO()
    }
}
