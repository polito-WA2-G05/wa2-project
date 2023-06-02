package it.polito.wa2.g05.server.products.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.products.dtos.ProductDTO
import it.polito.wa2.g05.server.products.services.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.slf4j.LoggerFactory

@Observed
@RestController
@RequestMapping("/api/public/products")
class ProductController(private val productService: ProductService) {

    private val log = LoggerFactory.getLogger("ProductController")

    /* GET /api/public/products */

    @GetMapping
    fun getAll(): List<ProductDTO> {
        log.info("Getting the list of all products")
        return productService.getAll()
    }

    /* GET /api/public/products/{ean} */

    @GetMapping("/{ean}")
    fun getProduct(@PathVariable("ean") ean: String): ProductDTO {
        log.info("Getting the product with ean = $ean")
        return productService.getProduct(ean)
    }
}