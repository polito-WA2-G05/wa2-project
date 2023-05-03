package it.polito.wa2.g05.server.product;

import it.polito.wa2.g05.server.products.*
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

    @Autowired
    lateinit var productService: ProductService

    private final val product1 = Product("4935531465706", "TestProduct1", "TestBrand1")
    private final val product2 = Product("1547869532126", "TestProduct2", "TestBrand2")
    private final val product3 = Product("1645876452315", "TestProduct3", "TestBrand3")

    @BeforeEach
    fun insertData() {
        productRepository.deleteAll()

        productRepository.save(product1)
        productRepository.save(product2)
        productRepository.save(product3)
    }

    @Test
    fun getAllProducts() {
        val products = productService.getAll()

        val expectedProducts = listOf(
            product1.toDTO(),
            product2.toDTO(),
            product3.toDTO()
        )

        Assertions.assertEquals(expectedProducts, products)
    }

    @Test
    fun getProductByEan() {
        val product = productService.getProduct(product1.ean)
        Assertions.assertEquals(product1.toDTO(), product)
    }

    @Test
    fun getAllProductsEmptyTable() {
        productRepository.deleteAll()
        val products = productService.getAll()

        val expectedProducts = listOf<ProductDTO>()

        Assertions.assertEquals(expectedProducts, products)
    }

    @Test
    fun getProductByEanNotFound() {
        Assertions.assertThrows(ProductNotFoundException::class.java, {
            productService.getProduct("2456513265978")
        }, "Product with ean equals to 2456513265978 not found")
    }
}
