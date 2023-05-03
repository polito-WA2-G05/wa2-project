package it.polito.wa2.g05.server.tickets;

import it.polito.wa2.g05.server.products.Product
import it.polito.wa2.g05.server.products.ProductNotFoundException
import it.polito.wa2.g05.server.products.ProductRepository
import it.polito.wa2.g05.server.products.toDTO
import it.polito.wa2.g05.server.profiles.Profile
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.profiles.ProfileRepository
import it.polito.wa2.g05.server.profiles.toDTO
import it.polito.wa2.g05.server.tickets.dtos.CreateTicketFormDTO
import it.polito.wa2.g05.server.tickets.dtos.StartTicketFormDTO
import it.polito.wa2.g05.server.tickets.dtos.toDTO
import it.polito.wa2.g05.server.tickets.entities.Employee
import it.polito.wa2.g05.server.tickets.entities.Specialization
import it.polito.wa2.g05.server.tickets.entities.Ticket
import it.polito.wa2.g05.server.tickets.repositories.ChangeRepository
import it.polito.wa2.g05.server.tickets.repositories.EmployeeRepository
import it.polito.wa2.g05.server.tickets.repositories.SpecializationRepository
import it.polito.wa2.g05.server.tickets.repositories.TicketRepository
import it.polito.wa2.g05.server.tickets.services.TicketService
import it.polito.wa2.g05.server.tickets.utils.PriorityLevel
import it.polito.wa2.g05.server.tickets.utils.TicketStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.Date
import kotlin.random.Random

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IntegrationTest {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var ticketRepository: TicketRepository

    @Autowired
    lateinit var profileRepository: ProfileRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var specializationRepository: SpecializationRepository

    @Autowired
    lateinit var employeeRepository: EmployeeRepository

    @Autowired
    lateinit var changeRepository: ChangeRepository

    @Autowired
    lateinit var ticketService: TicketService

    private final val profile = Profile("Test", "Test", "test@test.it")
    private final val product1 = Product("4935531465706", "TestProduct1", "TestBrand1")
    private final val product2 = Product("4935531468592", "TestProduct2", "TestBrand2")
    private final val specialization = Specialization("Computer")
    private final val expert = Employee("expert@test.it", "EXPERT", mutableSetOf(specialization), 0)

    protected fun saveTicket(
        status: TicketStatus,
        expert: Employee? = null,
        priorityLevel: PriorityLevel? = null,
        product: Product = product1
    ): Ticket {
        val ticketEntity = Ticket(
            status,
            "TestTicket",
            "Description",
            profile,
            expert,
            priorityLevel,
            product,
            Date(),
            null,
            specialization
        )

        return ticketRepository.save(ticketEntity)
    }

    @BeforeEach
    fun insertData() {
        changeRepository.deleteAll()
        ticketRepository.deleteAll()
        profileRepository.deleteAll()
        productRepository.deleteAll()
        employeeRepository.deleteAll()
        specializationRepository.deleteAll()

        profileRepository.save(profile)
        productRepository.save(product1)
        productRepository.save(product2)
        specializationRepository.save(specialization)
        employeeRepository.save(expert)
    }

    // Create Ticket Tests

    @Test
    fun createTicket() {
        val profiles = profileRepository.findAll()
        val specializations = specializationRepository.findAll()
        val products = productRepository.findAll()

        val data = CreateTicketFormDTO(
            title = "Problema con il prodotto",
            description = "Non riesco ad utilizzare correttamente il prodotto",
            customerId = profiles[0].getId(),
            productEAN = products[0].ean,
            specializationId = specializations[0].getId()
        )

        val res = ticketService.createTicket(data)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertNotEquals(null, res.id)
        Assertions.assertEquals(TicketStatus.OPEN, res.status)
        Assertions.assertEquals(data.title, res.title)
        Assertions.assertEquals(data.description, res.description)
        Assertions.assertEquals(profiles[0].toDTO(), res.customer)
        Assertions.assertEquals(null, res.expert)
        Assertions.assertEquals(null, res.priorityLevel)
        Assertions.assertEquals(products[0].toDTO(), res.product)
        Assertions.assertNotEquals(null, res.createdDate)
        Assertions.assertEquals(null, res.closedDate)
        Assertions.assertEquals(specializations[0].toDTO(), res.specialization)

        Assertions.assertEquals(1, changes.size)
        Assertions.assertEquals(null, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.OPEN, changes[0].toStatus)
    }

    @Test
    fun createTicketWithProfileThatNotExists() {
        val profileIds = profileRepository.findAll().map { it.getId() }
        val productEans = productRepository.findAll().map { it.ean }
        val specializationIds = specializationRepository.findAll().map { it.getId() }

        var profileThatNotExists: Long = Random.nextLong();

        while (profileIds.contains(profileThatNotExists)) {
            profileThatNotExists = Random.nextLong()
        }

        val data = CreateTicketFormDTO(
            title = "Problema con il prodotto",
            description = "Non riesco ad utilizzare correttamente il prodotto",
            customerId = profileThatNotExists,
            productEAN = productEans[0],
            specializationId = specializationIds[0]
        )

        Assertions.assertThrows(ProfileNotFoundException::class.java, {
            ticketService.createTicket(data)
        }, "Profile $profileThatNotExists not found")
    }

    @Test
    fun createTicketWithProductThatNotExists() {
        val profileIds = profileRepository.findAll().map { it.getId() }
        val productEans = productRepository.findAll().map { it.ean }
        val specializationIds = specializationRepository.findAll().map { it.getId() }

        var productThatNotExists: String = Random.nextLong().toString()

        while (productEans.contains(productThatNotExists)) {
            productThatNotExists = Random.nextLong().toString()
        }

        val data = CreateTicketFormDTO(
            title = "Problema con il prodotto",
            description = "Non riesco ad utilizzare correttamente il prodotto",
            customerId = profileIds[0],
            productEAN = productThatNotExists,
            specializationId = specializationIds[0]
        )

        Assertions.assertThrows(ProductNotFoundException::class.java, {
            ticketService.createTicket(data)
        }, "Product $productThatNotExists not found")
    }

    @Test
    fun createTicketWithSpecializationThatNotExists() {
        val profileIds = profileRepository.findAll().map { it.getId() }
        val productEans = productRepository.findAll().map { it.ean }
        val specializationIds = specializationRepository.findAll().map { it.getId() }

        var specializationThatNotExists: Long = Random.nextLong();

        while (specializationIds.contains(specializationThatNotExists)) {
            specializationThatNotExists = Random.nextLong()
        }

        val data = CreateTicketFormDTO(
            title = "Problema con il prodotto",
            description = "Non riesco ad utilizzare correttamente il prodotto",
            customerId = profileIds[0],
            productEAN = productEans[0],
            specializationId = specializationThatNotExists
        )

        Assertions.assertThrows(SpecializationNotFoundException::class.java, {
            ticketService.createTicket(data)
        }, "Specialization $specializationThatNotExists not found")
    }

    // Cancel Ticket Tests

    @Test
    fun cancelTicketFromOpen() {
        val ticket = this.saveTicket(TicketStatus.OPEN)

        val res = ticketService.cancelTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.CANCELLED, res.status)
        Assertions.assertEquals(null, res.expert)

        Assertions.assertEquals(TicketStatus.OPEN, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.CANCELLED, changes[0].toStatus)
    }

    @Test
    fun cancelTicketFromInProgress() {
        val ticket = this.saveTicket(TicketStatus.IN_PROGRESS, expert, PriorityLevel.LOW)

        val res = ticketService.cancelTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.CANCELLED, res.status)

        Assertions.assertEquals(TicketStatus.IN_PROGRESS, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.CANCELLED, changes[0].toStatus)
    }

    @Test
    fun cancelFromReopened() {
        val ticket = this.saveTicket(TicketStatus.REOPENED)

        val res = ticketService.cancelTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.CANCELLED, res.status)

        Assertions.assertEquals(TicketStatus.REOPENED, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.CANCELLED, changes[0].toStatus)
    }

    @Test
    fun cancelTicketFromResolved() {
        val ticket = this.saveTicket(TicketStatus.RESOLVED)

        val res = ticketService.cancelTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.CANCELLED, res.status)

        Assertions.assertEquals(TicketStatus.RESOLVED, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.CANCELLED, changes[0].toStatus)
    }

    @Test
    fun cancelTicketFromClosed() {
        val ticket = this.saveTicket(TicketStatus.CLOSED)

        val res = ticketService.cancelTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.CANCELLED, res.status)

        Assertions.assertEquals(TicketStatus.CLOSED, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.CANCELLED, changes[0].toStatus)
    }

    @Test
    fun invalidCancelTicketFromCancelled() {
        val ticket = this.saveTicket(TicketStatus.CANCELLED)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.cancelTicket(ticket.getId()!!)
        }, "Status can't be set to CANCELLED from CANCELLED")
    }

    // Close Ticket Tests

    @Test
    fun closeTicketFromOpen() {
        val ticket = this.saveTicket(TicketStatus.OPEN)

        val res = ticketService.closeTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.CLOSED, res.status)

        Assertions.assertEquals(TicketStatus.OPEN, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.CLOSED, changes[0].toStatus)
    }

    @Test
    fun closeTicketFromResolved() {
        val ticket = this.saveTicket(TicketStatus.RESOLVED)

        val res = ticketService.closeTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.CLOSED, res.status)

        Assertions.assertEquals(TicketStatus.RESOLVED, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.CLOSED, changes[0].toStatus)
    }

    @Test
    fun closeTicketFromReopened() {
        val ticket = this.saveTicket(TicketStatus.REOPENED)

        val res = ticketService.closeTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.CLOSED, res.status)

        Assertions.assertEquals(TicketStatus.REOPENED, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.CLOSED, changes[0].toStatus)
    }

    @Test
    fun closeTicketFromInProgress() {
        val ticket = this.saveTicket(TicketStatus.IN_PROGRESS, expert, PriorityLevel.HIGH)

        val res = ticketService.closeTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.CLOSED, res.status)

        Assertions.assertEquals(TicketStatus.IN_PROGRESS, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.CLOSED, changes[0].toStatus)
    }

    @Test
    fun invalidCloseTicketFromCancelled() {
        val ticket = this.saveTicket(TicketStatus.CANCELLED)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.closeTicket(ticket.getId()!!)
        }, "Status can't be set to CLOSED from CANCELLED")
    }

    @Test
    fun invalidCloseTicketFromClosed() {
        val ticket = this.saveTicket(TicketStatus.CLOSED)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.closeTicket(ticket.getId()!!)
        }, "Status can't be set to CLOSED from CLOSED")
    }

    // Reopen Ticket Tests

    @Test
    fun reopenTicketFromResolved() {
        val ticket = this.saveTicket(TicketStatus.RESOLVED)

        val res = ticketService.reopenTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.REOPENED, res.status)

        Assertions.assertEquals(TicketStatus.RESOLVED, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.REOPENED, changes[0].toStatus)
    }

    @Test
    fun reopenTicketFromClosed() {
        val ticket = this.saveTicket(TicketStatus.CLOSED)

        val res = ticketService.reopenTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.REOPENED, res.status)

        Assertions.assertEquals(TicketStatus.CLOSED, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.REOPENED, changes[0].toStatus)
    }

    @Test
    fun invalidReopenTicketFromOpen() {
        val ticket = this.saveTicket(TicketStatus.OPEN)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.reopenTicket(ticket.getId()!!)
        }, "Status can't be set to REOPENED from OPENED")
    }

    @Test
    fun invalidReopenTicketFromInProgress() {
        val ticket = this.saveTicket(TicketStatus.IN_PROGRESS)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.reopenTicket(ticket.getId()!!)
        }, "Status can't be set to REOPENED from IN_PROGRESS")
    }

    @Test
    fun invalidReopenTicketFromCancelled() {
        val ticket = this.saveTicket(TicketStatus.CANCELLED)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.reopenTicket(ticket.getId()!!)
        }, "Status can't be set to REOPENED from CANCELLED")
    }

    @Test
    fun invalidReopenTicketFromReopened() {
        val ticket = this.saveTicket(TicketStatus.REOPENED)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.reopenTicket(ticket.getId()!!)
        }, "Status can't be set to REOPENED from REOPENED")
    }

    // Start Ticket Tests

    @Test
    fun startTicketFromOpen() {
        val ticket = this.saveTicket(TicketStatus.OPEN)
        val data = StartTicketFormDTO(1)

        val res = ticketService.startTicket(ticket.getId()!!, data)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.IN_PROGRESS, res.status)
        Assertions.assertNotEquals(null, res.expert)
        Assertions.assertEquals(true, res.expert?.specializations?.contains(specialization.toDTO()) ?: Boolean)

        Assertions.assertEquals(TicketStatus.OPEN, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.IN_PROGRESS, changes[0].toStatus)
    }

    @Test
    fun startTicketFromReopened() {
        val ticket = this.saveTicket(TicketStatus.REOPENED)
        val data = StartTicketFormDTO(1)

        val res = ticketService.startTicket(ticket.getId()!!, data)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.IN_PROGRESS, res.status)
        Assertions.assertNotEquals(null, res.expert)
        Assertions.assertEquals(true, res.expert?.specializations?.contains(specialization.toDTO()) ?: Boolean)

        Assertions.assertEquals(TicketStatus.REOPENED, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.IN_PROGRESS, changes[0].toStatus)
    }


    @Test
    fun invalidStartTicketFromInProgress() {
        val ticket = this.saveTicket(TicketStatus.IN_PROGRESS)
        val data = StartTicketFormDTO(1)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.startTicket(ticket.getId()!!, data)
        }, "Status can't be set to IN_PROGRESS from IN_PROGRESS")
    }

    @Test
    fun invalidStartTicketFromResolved() {
        val ticket = this.saveTicket(TicketStatus.RESOLVED)
        val data = StartTicketFormDTO(1)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.startTicket(ticket.getId()!!, data)
        }, "Status can't be set to IN_PROGRESS from RESOLVED")
    }

    @Test
    fun invalidStartTicketFromClosed() {
        val ticket = this.saveTicket(TicketStatus.CLOSED)
        val data = StartTicketFormDTO(1)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.startTicket(ticket.getId()!!, data)
        }, "Status can't be set to IN_PROGRESS from CLOSED")
    }

    @Test
    fun invalidStartTicketFromCancelled() {
        val ticket = this.saveTicket(TicketStatus.CANCELLED)
        val data = StartTicketFormDTO(1)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.startTicket(ticket.getId()!!, data)
        }, "Status can't be set to IN_PROGRESS from CANCELLED")
    }

    // Stop Ticket Tests

    @Test
    fun stopTicketFromInProgress() {
        val ticket = this.saveTicket(TicketStatus.IN_PROGRESS)

        val res = ticketService.stopTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.OPEN, res.status)
        Assertions.assertEquals(null, res.expert)

        Assertions.assertEquals(TicketStatus.IN_PROGRESS, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.OPEN, changes[0].toStatus)
    }

    @Test
    fun invalidStopTicketFromOpen() {
        val ticket = this.saveTicket(TicketStatus.OPEN)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.stopTicket(ticket.getId()!!)
        }, "Status can't be set to OPEN from OPENED")
    }

    @Test
    fun invalidStopTicketFromClosed() {
        val ticket = this.saveTicket(TicketStatus.CLOSED)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.stopTicket(ticket.getId()!!)
        }, "Status can't be set to OPEN from CLOSED")
    }

    @Test
    fun invalidStopTicketFromResolved() {
        val ticket = this.saveTicket(TicketStatus.RESOLVED)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.stopTicket(ticket.getId()!!)
        }, "Status can't be set to OPEN from RESOLVED")
    }

    @Test
    fun invalidStopTicketFromReopened() {
        val ticket = this.saveTicket(TicketStatus.REOPENED)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.stopTicket(ticket.getId()!!)
        }, "Status can't be set to OPEN from RESOLVED")
    }

    @Test
    fun invalidStopTicketFromCancelled() {
        val ticket = this.saveTicket(TicketStatus.CANCELLED)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.stopTicket(ticket.getId()!!)
        }, "Status can't be set to OPEN from RESOLVED")
    }

    // Resolve Ticket Tests

    @Test
    fun resolveTicketFromOpened() {
        val ticket = this.saveTicket(TicketStatus.OPEN)

        val res = ticketService.resolveTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.RESOLVED, res.status)
        Assertions.assertEquals(null, res.expert)

        Assertions.assertEquals(TicketStatus.OPEN, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.RESOLVED, changes[0].toStatus)
    }

    @Test
    fun resolveTicketFromReopened() {
        val ticket = this.saveTicket(TicketStatus.REOPENED)

        val res = ticketService.resolveTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.RESOLVED, res.status)
        Assertions.assertEquals(null, res.expert)

        Assertions.assertEquals(TicketStatus.REOPENED, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.RESOLVED, changes[0].toStatus)
    }

    @Test
    fun resolveTicketFromInProgress() {
        val ticket = this.saveTicket(TicketStatus.IN_PROGRESS)

        val res = ticketService.resolveTicket(ticket.getId()!!)

        val changes = changeRepository.findAll().filter { it.ticket?.getId() == res.id }

        Assertions.assertEquals(TicketStatus.RESOLVED, res.status)

        Assertions.assertEquals(TicketStatus.IN_PROGRESS, changes[0].fromStatus)
        Assertions.assertEquals(TicketStatus.RESOLVED, changes[0].toStatus)
    }

    @Test
    fun invalidResolveTicketFromClosed() {
        val ticket = this.saveTicket(TicketStatus.CLOSED)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.resolveTicket(ticket.getId()!!)
        }, "Status can't be set to RESOLVED from CLOSED")
    }

    @Test
    fun invalidResolveTicketFromCancelled() {
        val ticket = this.saveTicket(TicketStatus.CANCELLED)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.resolveTicket(ticket.getId()!!)
        }, "Status can't be set to RESOLVED from CANCELLED")
    }

    @Test
    fun invalidResolveTicketFromResolved() {
        val ticket = this.saveTicket(TicketStatus.RESOLVED)

        Assertions.assertThrows(TicketStatusNotValidException::class.java, {
            ticketService.resolveTicket(ticket.getId()!!)
        }, "Status can't be set to RESOLVED from RESOLVED")
    }

    // Get Ticket Tests

    @Test
    fun getTicket() {
        val ticket = this.saveTicket(TicketStatus.OPEN)
        val res = ticketService.getTicket(ticket.getId()!!)
        Assertions.assertEquals(ticket.getId(), res.id)
    }

    @Test
    fun getTicketThatNotExists() {
        val id = Random.nextLong()

        Assertions.assertThrows(TicketNotFoundException::class.java, {
            ticketService.getTicket(id)
        }, "Ticket $id not found")
    }

    // Get Tickets by product Tests

    @Test
    fun getTicketsByProductId() {
        val product1Id = productRepository.findByEan(product1.ean).get().getId()
        val product2Id = productRepository.findByEan(product2.ean).get().getId()

        for (i in 0..2) {
            this.saveTicket(TicketStatus.OPEN, product = product1)
        }

        for (i in 0..5) {
            this.saveTicket(TicketStatus.OPEN, product = product2)
        }

        val resByProduct1 = ticketService.getAllTicketsByProductId(product1Id!!)
        val resByProduct2 = ticketService.getAllTicketsByProductId(product2Id!!)

        Assertions.assertEquals(3, resByProduct1.size)
        Assertions.assertEquals(6, resByProduct2.size)
    }

    @Test
    fun getTicketsByProductEmptyTable() {
        val productIds = productRepository.findAll().map { it.getId()!! }

        val res = ticketService.getAllTicketsByProductId(productIds[0])

        Assertions.assertEquals(0, res.size)
    }

    @Test
    fun getTicketsByProductThatNotExists() {
        val productIds = productRepository.findAll().map { it.getId() }

        var productThatNotExists: Long = Random.nextLong();

        while (productIds.contains(productThatNotExists)) {
            productThatNotExists = Random.nextLong()
        }

        for (i in 0..2) {
            this.saveTicket(TicketStatus.OPEN, product = product1)
        }

        Assertions.assertThrows(ProductNotFoundException::class.java, {
            ticketService.getAllTicketsByProductId(productThatNotExists)
        }, "Ticket $productIds not found")
    }
}


