package it.polito.wa2.g05.server.products

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    // /api/products
    @GetMapping
    fun getAll(): List<ProductDTO> {
        return productService.getAll()
    }

    // /api/products/{ean}
    @GetMapping("/{ean}")
    fun getProduct(@PathVariable("ean") ean: String): ProductDTO {
        return productService.getProduct(ean)
    }
}