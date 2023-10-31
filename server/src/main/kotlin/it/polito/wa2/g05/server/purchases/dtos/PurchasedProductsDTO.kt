package it.polito.wa2.g05.server.purchases.dtos

import it.polito.wa2.g05.server.products.dtos.ProductDTO
import it.polito.wa2.g05.server.products.dtos.toDTO
import it.polito.wa2.g05.server.purchases.entities.Purchase
import java.util.Date

data class PurchasedProductsDTO (
    val products: MutableSet<ProductDTO>,
    val purchasedAt: Date,
)

fun Purchase.toDTO(): PurchasedProductsDTO {
    return PurchasedProductsDTO(products.map { it.toDTO() }.toMutableSet(), purchasedAt)
}