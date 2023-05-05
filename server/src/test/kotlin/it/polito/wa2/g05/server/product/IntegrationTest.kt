package it.polito.wa2.g05.server.product

import it.polito.wa2.g05.server.products.Product
import it.polito.wa2.g05.server.products.ProductRepository
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
    lateinit var productRepository: ProductRepository

    private final val product = Product("4935531465706", "TestProduct1", "TestBrand1")

    @BeforeEach
    fun insertData() {
        productRepository.deleteAll()

        productRepository.save(product)
    }

    @Test
    fun getProducts() {
        val headers = HttpHeaders()
        val requestEntity= HttpEntity<Unit>(headers)

        val response = restTemplate.exchange(
            "http://localhost:$port/api/products",
            HttpMethod.GET,
            requestEntity,
            List::class.java
        )

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }
    
    @Test
    fun getProductByEan() {
        val headers = HttpHeaders()
        val requestEntity= HttpEntity<Unit>(headers)

        val response = restTemplate.exchange(
            "http://localhost:$port/api/products/${product.ean}",
            HttpMethod.GET,
            requestEntity,
            Product::class.java
        )

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getProductNotFound() {
        val headers = HttpHeaders()
        val requestEntity= HttpEntity<Unit>(headers)

        val response = restTemplate.exchange(
            "http://localhost:$port/api/products/fakeEan",
            HttpMethod.GET,
            requestEntity,
            Product::class.java
        )

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}