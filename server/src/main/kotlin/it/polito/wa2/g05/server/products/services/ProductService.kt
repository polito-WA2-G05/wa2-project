package it.polito.wa2.g05.server.products.services

import it.polito.wa2.g05.server.products.dtos.ProductDTO

interface ProductService {
    fun getAll(): List<ProductDTO>
    fun getProduct(ean: String): ProductDTO
}