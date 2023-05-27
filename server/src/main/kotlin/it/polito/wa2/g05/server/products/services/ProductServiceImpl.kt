package it.polito.wa2.g05.server.products.services

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.products.ProductNotFoundException
import it.polito.wa2.g05.server.products.dtos.ProductDTO
import it.polito.wa2.g05.server.products.dtos.toDTO
import it.polito.wa2.g05.server.products.repositories.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Observed
@Service
class ProductServiceImpl(private val productRepository: ProductRepository): ProductService {
    private val log = LoggerFactory.getLogger("ProductServiceImpl")
    override fun getAll(): List<ProductDTO> =
        productRepository.findAll().map { it.toDTO() }

    override fun getProduct(ean: String): ProductDTO =
        productRepository.findByEan(ean).orElseThrow {
            log.error("Product $ean not found error")
            throw ProductNotFoundException("Product with ean equals to $ean not found")
        }.toDTO()
}