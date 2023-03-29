package it.polito.wa2.g05.server.products

import org.springframework.stereotype.Service

@Service
class ProductServiceImpl(private val productRepository: ProductRepository): ProductService {
    override fun getAll(): List<ProductDTO> {
        return productRepository.findAll().map { it.toDTO() }
    }

    override fun getProduct(ean: String): ProductDTO {
        val product = productRepository.findById(ean).map { it.toDTO() }

        if (product.isEmpty)
            throw ProductNotFoundException("Product with ean equals to $ean not found")

        return product.get()
    }
}