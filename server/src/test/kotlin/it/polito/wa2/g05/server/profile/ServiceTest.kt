package it.polito.wa2.g05.server.profiles

import it.polito.wa2.g05.server.profiles.Profile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServiceTest {
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

    @Autowired
    lateinit var profileRepository: ProfileRepository

    @Autowired
    lateinit var profileService: ProfileService

    private final val profile = Profile("Test_Nome", "Test_Cognome", "Test@test.it")


    @BeforeEach
    fun insertData() {
        profileRepository.deleteAll()
        profileRepository.save(profile)
    }

    @Test
    fun getUserByEmail() {
        val res = profileService.getProfile(profile.email)
        Assertions.assertEquals(profile.name, res.name)
        Assertions.assertEquals(profile.surname, res.surname)
        Assertions.assertEquals(profile.email, res.email)
    }

    @Test
    fun createUser() {
        val data = ProfileFormDTO("Test1", "Test1", "test1@test.it")
        val profile = profileService.createProfile(data)

        Assertions.assertNotEquals(null, profile.id)
        Assertions.assertEquals(data.name, profile.name)
        Assertions.assertEquals(data.surname, profile.surname)
        Assertions.assertEquals(data.email, profile.email)
    }

    @Test
    fun updateProfile() {
        val data = ProfileFormDTO("TestUpdated", "TestUpdated", "test1@test.it")

        val res = profileService.updateProfile(profile.email, data)

        Assertions.assertEquals(data.name, res.name)
        Assertions.assertEquals(data.surname, res.surname)
        Assertions.assertEquals(data.email, res.email)
    }

    @Test
    fun getUserByEmailNotFound() {
        Assertions.assertThrows(
            ProfileNotFoundException::class.java, {
                profileService.getProfile("notfound@test.it")
            }, "Profile not found with the notfound@test.it inserted"
        )
    }

    @Test
    fun profileAlreadyExisting() {
        val data = ProfileFormDTO("name", "surname", profile.email)
        Assertions.assertThrows(
            EmailAlreadyExistingException::class.java,
            { profileService.createProfile(data) },
            "A profile with ${profile.email} as email already exists"
        )
    }

    @Test
    fun updateProfileNotExisting() {
        val data = ProfileFormDTO("name", "surname", "test1@test.it")
        val notExistingEmail = "test111@test.it"

        Assertions.assertThrows(
            ProfileNotFoundException::class.java,
            { profileService.updateProfile(notExistingEmail, data) },
            "Profile not found with the $notExistingEmail provided"
        )
    }
}