package it.polito.wa2.g05.server.products.controllers

import it.polito.wa2.g05.server.products.dtos.ProductDTO
import it.polito.wa2.g05.server.products.services.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/products")
class ProductController(private val productService: ProductService) {

    // /api/public/products
    @GetMapping
    fun getAll(): List<ProductDTO> {
        return productService.getAll()
    }

    // /api/public/products/{ean}
    @GetMapping("/{ean}")
    fun getProduct(@PathVariable("ean") ean: String): ProductDTO {
        return productService.getProduct(ean)
    }
}