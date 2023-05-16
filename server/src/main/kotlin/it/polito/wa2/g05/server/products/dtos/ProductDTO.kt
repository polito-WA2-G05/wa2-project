package it.polito.wa2.g05.server.products.dtos

import it.polito.wa2.g05.server.products.entities.Product

data class ProductDTO(
    val ean: String,
    val name: String,
    val brand: String
)

fun Product.toDTO(): ProductDTO {
    return ProductDTO(ean, name, brand)
}