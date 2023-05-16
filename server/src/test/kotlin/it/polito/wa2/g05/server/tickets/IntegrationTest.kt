package it.polito.wa2.g05.server.tickets

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.g05.server.products.entities.Product
import it.polito.wa2.g05.server.products.repositories.ProductRepository
import it.polito.wa2.g05.server.profiles.entities.Profile
import it.polito.wa2.g05.server.profiles.repositories.ProfileRepository
import it.polito.wa2.g05.server.tickets.dtos.CreateTicketFormDTO
import it.polito.wa2.g05.server.tickets.dtos.StartTicketFormDTO
import it.polito.wa2.g05.server.tickets.dtos.TicketDTO
import it.polito.wa2.g05.server.tickets.entities.Employee
import it.polito.wa2.g05.server.tickets.entities.Specialization
import it.polito.wa2.g05.server.tickets.entities.Ticket
import it.polito.wa2.g05.server.tickets.repositories.ChangeRepository
import it.polito.wa2.g05.server.tickets.repositories.EmployeeRepository
import it.polito.wa2.g05.server.tickets.repositories.SpecializationRepository
import it.polito.wa2.g05.server.tickets.repositories.TicketRepository
import it.polito.wa2.g05.server.tickets.utils.PriorityLevel
import it.polito.wa2.g05.server.tickets.utils.TicketStatus
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
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

    final val client = OkHttpClient()

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    lateinit var restTemplate : TestRestTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper

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
        val profileIds = profileRepository.findAll().map { it.getId()!! }
        val productEans = productRepository.findAll().map { it.ean }
        val specializationIds = specializationRepository.findAll().map { it.getId()!! }

        val data = CreateTicketFormDTO(
            "Broken iPhone",
            "The phone is not turning on since yesterday",
            profileIds[0],
            productEans[0],
            specializationIds[0]
        )

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, TicketDTO::class.java
        )

        Assertions.assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun createTicketWithCustomerThatNotExists() {
        val profileIds = profileRepository.findAll().map { it.getId()!! }
        val productEans = productRepository.findAll().map { it.ean }
        val specializationIds = specializationRepository.findAll().map { it.getId()!! }

        var profileThatNotExists: Long = Random.nextLong()

        while (profileIds.contains(profileThatNotExists) || profileThatNotExists <= 0) {
            profileThatNotExists = Random.nextLong()
        }

        val data = CreateTicketFormDTO(
            "Broken iPhone",
            "The phone is not turning on since yesterday",
            profileThatNotExists,
            productEans[0],
            specializationIds[0]
        )

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, Any::class.java
        )

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun createTicketWithProductThatNotExist() {
        val profileIds = profileRepository.findAll().map { it.getId()!! }
        val productEans = productRepository.findAll().map { it.ean }
        val specializationIds = specializationRepository.findAll().map { it.getId()!! }

        var productThatNotExists: String = Random.nextLong().toString()

        while (productEans.contains(productThatNotExists)) {
            productThatNotExists = Random.nextLong().toString()
        }

        val data = CreateTicketFormDTO(
            "Broken iPhone",
            "The phone is not turning on since yesterday",
            profileIds[0],
            productThatNotExists,
            specializationIds[0]
        )


        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, Any::class.java
        )

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }


    @Test
    fun createTicketWithSpecializationThatNotExist() {
        val profileIds = profileRepository.findAll().map { it.getId()!! }
        val productEans = productRepository.findAll().map { it.ean }
        val specializationIds = specializationRepository.findAll().map { it.getId()!! }
        var specializationThatNotExists: Long = Random.nextLong()

        while (specializationIds.contains(specializationThatNotExists) || specializationThatNotExists <= 0) {
            specializationThatNotExists = Random.nextLong()
        }

        val data = CreateTicketFormDTO(
            "Broken iPhone",
            "The phone is not turning on since yesterday",
            profileIds[0],
            productEans[0],
            specializationThatNotExists
        )

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, Any::class.java
        )

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun createTicketBlankTitle() {
        val profileIds = profileRepository.findAll().map { it.getId()!! }
        val productEans = productRepository.findAll().map { it.ean }
        val specializationIds = specializationRepository.findAll().map { it.getId()!! }

        val data = CreateTicketFormDTO(
            "",
            "The phone is not turning on since yesterday",
            profileIds[0],
            productEans[0],
            specializationIds[0]
        )

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, Any::class.java
        )

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun createTicketNullTitle() {
        val profileIds = profileRepository.findAll().map { it.getId()!! }
        val productEans = productRepository.findAll().map { it.ean }
        val specializationIds = specializationRepository.findAll().map { it.getId()!! }

        val data = CreateTicketFormDTO(
            null,
            "The phone is not turning on since yesterday",
            profileIds[0],
            productEans[0],
            specializationIds[0]
        )

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, Any::class.java
        )

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun createTicketBlankDescription() {
        val profileIds = profileRepository.findAll().map { it.getId()!! }
        val productEans = productRepository.findAll().map { it.ean }
        val specializationIds = specializationRepository.findAll().map { it.getId()!! }

        val data = CreateTicketFormDTO(
            "Broken iPhone",
            "",
            profileIds[0],
            productEans[0],
            specializationIds[0]
        )

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, Any::class.java
        )

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun createTicketNullDescription() {
        val profileIds = profileRepository.findAll().map { it.getId()!! }
        val productEans = productRepository.findAll().map { it.ean }
        val specializationIds = specializationRepository.findAll().map { it.getId()!! }

        val data = CreateTicketFormDTO(
            "broken Iphone",
            null,
            profileIds[0],
            productEans[0],
            specializationIds[0]
        )

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, Any::class.java
        )

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun createTicketNegativeCustomerId() {
        val productEans = productRepository.findAll().map { it.ean }
        val specializationIds = specializationRepository.findAll().map { it.getId()!! }

        val data = CreateTicketFormDTO(
            "Broken iPhone",
            "The phone is not turning on since yesterday",
            -20,
            productEans[0],
            specializationIds[0]
        )

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, Any::class.java
        )

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun createTicketBlankProductEAN() {
        val profileIds = profileRepository.findAll().map { it.getId()!! }
        val specializationIds = specializationRepository.findAll().map { it.getId()!! }

        val data = CreateTicketFormDTO(
            "Broken iphone",
            "The phone is not turning on since yesterday",
            profileIds[0],
            "",
            specializationIds[0]
        )

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, Any::class.java
        )

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun createTicketNullProduct() {
        val profileIds = profileRepository.findAll().map { it.getId()!! }
        val specializationIds = specializationRepository.findAll().map { it.getId()!! }

        val data = CreateTicketFormDTO(
            "Broken iPhone",
            "The phone is not turning on since yesterday",
            profileIds[0],
            null,
            specializationIds[0]
        )

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, Any::class.java
        )

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun createTicketNegativeSpecializationId() {
        val profileIds = profileRepository.findAll().map { it.getId()!! }
        val productEans = productRepository.findAll().map { it.ean }

        val data = CreateTicketFormDTO(
            "Broken iPhone",
            "The phone is not turning on since yesterday",
            profileIds[0],
            productEans[0],
            -37
        )

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, Any::class.java
        )

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }


    @Test
    fun createTicketNullSpecialization() {
        val profileIds = profileRepository.findAll().map { it.getId()!! }
        val productEans = productRepository.findAll().map { it.ean }

        val data = CreateTicketFormDTO(
            "Brokien iphone",
            "The phone is not turning on since yesterday",
            profileIds[0],
            productEans[0],
            null
        )

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<CreateTicketFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/tickets", HttpMethod.POST, requestEntity, Any::class.java
        )

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    // Cancel Ticket Tests

    @Test
    fun cancelTicket() {
        val ticket = this.saveTicket(TicketStatus.OPEN)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/cancel")
            .patch(RequestBody.create(null, ""))
            .build()

        val response = client.newCall(request).execute()


        Assertions.assertEquals(HttpStatus.OK.value(), response.code())
    }

    @Test
    fun cancelTicketStatusTransitionError() {
        val ticket = this.saveTicket(TicketStatus.CANCELLED)
        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/cancel")
            .patch(RequestBody.create(null, ""))
            .build()

        val response = client.newCall(request).execute()


        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.code())
    }

    // Close Ticket Tests

    @Test
    fun closeTicket() {
        val ticket = this.saveTicket(TicketStatus.RESOLVED)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/close")
            .patch(RequestBody.create(null, ""))
            .build()

        val response = client.newCall(request).execute()

        Assertions.assertEquals(HttpStatus.OK.value(), response.code())
    }

    @Test
    fun closeTicketStatusTransitionError() {
        val ticket = this.saveTicket(TicketStatus.CLOSED)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/close")
            .patch(RequestBody.create(null, ""))
            .build()

        val response = client.newCall(request).execute()

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.code())
    }

    // Reopen Ticket Tests
    @Test
    fun reopenTicket() {
        val ticket = this.saveTicket(TicketStatus.RESOLVED)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/reopen")
            .patch(RequestBody.create(null, ""))
            .build()

        val response = client.newCall(request).execute()

        Assertions.assertEquals(HttpStatus.OK.value(), response.code())
    }

    @Test
    fun reopenTicketStatusTransitionError() {
        val ticket = this.saveTicket(TicketStatus.REOPENED)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/reopen")
            .patch(RequestBody.create(null, ""))
            .build()

        val response = client.newCall(request).execute()

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.code())
    }

    // Start Ticket Tests

    @Test
    fun startTicket() {
        val ticket = this.saveTicket(TicketStatus.OPEN)
        val data = StartTicketFormDTO(0)

        val requestBody = objectMapper.writeValueAsString(data)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/start")
            .patch(RequestBody.create(MediaType.parse("application/json"), requestBody))
            .build()

        val response = client.newCall(request).execute()

        Assertions.assertEquals(HttpStatus.OK.value(), response.code())
    }

    @Test
    fun startTicketPriorityLevelError() {
        val ticket = this.saveTicket(TicketStatus.OPEN)
        val data = StartTicketFormDTO(4)

        val requestBody = objectMapper.writeValueAsString(data)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/start")
            .patch(RequestBody.create(MediaType.parse("application/json"), requestBody))
            .build()

        val response = client.newCall(request).execute()

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.code())
    }

    @Test
    fun startTicketNegativePriorityLevel() {
        val ticket = this.saveTicket(TicketStatus.OPEN)
        val data = StartTicketFormDTO(-32)
        val requestBody = objectMapper.writeValueAsString(data)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/start")
            .patch(RequestBody.create(MediaType.parse("application/json"), requestBody))
            .build()

        val response = client.newCall(request).execute()

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.code())
    }

    @Test
    fun startTicketStatusTransitionError() {
        val ticket = this.saveTicket(TicketStatus.IN_PROGRESS)
        val data = StartTicketFormDTO(0)

        val requestBody = objectMapper.writeValueAsString(data)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/start")
            .patch(RequestBody.create(MediaType.parse("application/json"), requestBody))
            .build()

        val response = client.newCall(request).execute()

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.code())
    }

    // Stop Ticket Tests

    @Test
    fun stopTicket() {
        val ticket = this.saveTicket(TicketStatus.IN_PROGRESS)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/stop")
            .patch(RequestBody.create(null, ""))
            .build()

        val response = client.newCall(request).execute()

        Assertions.assertEquals(HttpStatus.OK.value(), response.code())
    }

    @Test
    fun stopTicketStatusTransitionError() {
        val ticket = this.saveTicket(TicketStatus.OPEN)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/stop")
            .patch(RequestBody.create(null, ""))
            .build()

        val response = client.newCall(request).execute()

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.code())
    }

    // Resolve Ticket Tests

    @Test
    fun resolveTicket() {
        val ticket = this.saveTicket(TicketStatus.OPEN)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/resolve")
            .patch(RequestBody.create(null, ""))
            .build()

        val response = client.newCall(request).execute()

        Assertions.assertEquals(HttpStatus.OK.value(), response.code())
    }

    @Test
    fun resolveTicketStatusTransitionError() {
        val ticket = this.saveTicket(TicketStatus.RESOLVED)

        val request = Request.Builder()
            .url("http://localhost:$port/api/tickets/${ticket.getId()!!}/resolve")
            .patch(RequestBody.create(null, ""))
            .build()

        val response = client.newCall(request).execute()

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.code())
    }

    // Get Ticket Tests

    @Test
    fun getTicketById() {
        val ticket = this.saveTicket(TicketStatus.OPEN)

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<Unit>(headers)
        val res = restTemplate.exchange(
            "http://localhost:$port/api/tickets/${ticket.getId()!!}",
            HttpMethod.GET,
            requestEntity,
            TicketDTO::class.java
        )

        Assertions.assertEquals(HttpStatus.OK, res.statusCode)
    }

    @Test
    fun getTicketThatNotExists() {
        val headers = HttpHeaders()
        val requestEntity = HttpEntity<Unit>(headers)
        val res = restTemplate.exchange(
            "http://localhost:$port/api/tickets/${Random.nextLong()}",
            HttpMethod.GET,
            requestEntity,
            Any::class.java
        )

        Assertions.assertEquals(HttpStatus.NOT_FOUND, res.statusCode)
    }

    // Get Tickets by Product Tests

    @Test
    fun getAllTicketsByGivenProductId() {

        for (i in 0..2) {
            this.saveTicket(TicketStatus.OPEN)
        }

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<Unit>(headers)
        val res = restTemplate.exchange(
            "http://localhost:$port/api/tickets?product=${product1.getId()!!}",
            HttpMethod.GET,
            requestEntity,
            List::class.java
            )
        Assertions.assertEquals(HttpStatus.OK, res.statusCode)
    }

    @Test
    fun getAllTicketsByProductIdNotExisting(){
        val productIds = productRepository.findAll().map { it.getId()!! }

        var productIdThatNotExists = Random.nextLong()

        while (productIds.contains(productIdThatNotExists)) {
            productIdThatNotExists = Random.nextLong()
        }

        val headers = HttpHeaders()
        val requestEntity = HttpEntity<Unit>(headers)
        val res = restTemplate.exchange(
            "http://localhost:$port/api/tickets?product=$productIdThatNotExists",
            HttpMethod.GET,
            requestEntity,
            Any::class.java
        )
        Assertions.assertEquals(HttpStatus.NOT_FOUND, res.statusCode)
    }
}