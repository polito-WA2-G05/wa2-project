package it.polito.wa2.g05.server.profile
import it.polito.wa2.g05.server.profiles.Profile
import it.polito.wa2.g05.server.profiles.ProfileFormDTO
import it.polito.wa2.g05.server.profiles.ProfileRepository
import it.polito.wa2.g05.server.profiles.ProfileService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

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
    lateinit var profileRepository: ProfileRepository

    @Autowired
    lateinit var profileService: ProfileService

    private final val profile = Profile("Test_Nome", "Test_Cognome", "Test@test.it")
    private final val profile1 = Profile("Test_Name1", "Test_Surname1", "Test1@test.it")

    @BeforeEach
    fun insertData() {
        profileRepository.deleteAll()
        profileRepository.save(profile)
        profileRepository.save(profile1)
    }

    @Test
    fun getUser() {
        val headers = HttpHeaders()
        val requestEntity= HttpEntity<Unit>(headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles/${profile.email}",
            HttpMethod.GET,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getUserNotExisting() {
        val email= "testttt@testtt.it"
        val headers = HttpHeaders()
        val requestEntity= HttpEntity<Unit>(headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles/${email}",
            HttpMethod.GET,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun insertUser(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "testt",
            "testt",
            "test2@test.it")
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles",
            HttpMethod.POST,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun insertUserValidateErrorNameBlank(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "",
            "testt",
            "test2@test.it")
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles",
            HttpMethod.POST,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun insertUserValidateErrorSurnameBlank(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "hello",
            "",
            "test2@test.it")
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles",
            HttpMethod.POST,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun insertUserValidateErrorEmailBlank(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "hello",
            "test",
            "")
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles",
            HttpMethod.POST,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun insertUserValidateErrorEmailNotEmail(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "hello",
            "test",
            "test")
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles",
            HttpMethod.POST,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }
    @Test
    fun insertUserValidateErrorEmailNull(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "hello",
            "test",
            null)
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles",
            HttpMethod.POST,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun insertUserValidateErrorNameNumbers(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "hello222",
            "test",
            "testtttttt@test.it")
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles",
            HttpMethod.POST, requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun updateUser(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "testt",
            "testt",
            "Test2@test.it")
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles/${profile.email}",
            HttpMethod.PUT,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun updateUserNotFoundEmail(){
        val email = "hellotest@test.it"
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "testt",
            "testt",
            "Test2@test.it")
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles/${email}",
            HttpMethod.PUT,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun updateUserBlankName(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "",
            "testt",
            "Test2@test.it")
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles/${profile.email}",
            HttpMethod.PUT,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun updateUserNullName(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            null,
            "testt",
            "Test2@test.it")
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles/${profile.email}",
            HttpMethod.PUT,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun updateUserBlankSurname(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "testt",
            "",
            "Test2@test.it")
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles/${profile.email}",
            HttpMethod.PUT,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun updateUserBlankEmail(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "testt",
            "",
            "")
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles/${profile.email}",
            HttpMethod.PUT,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }

    @Test
    fun updateUserNullEmail(){
        val headers = HttpHeaders()
        val data = ProfileFormDTO(
            "testt",
            "test",
            null)
        val requestEntity = HttpEntity<ProfileFormDTO>(data, headers)
        val response = restTemplate.exchange(
            "http://localhost:$port/api/profiles/${profile.email}",
            HttpMethod.PUT,
            requestEntity,
            Any::class.java)

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }
}