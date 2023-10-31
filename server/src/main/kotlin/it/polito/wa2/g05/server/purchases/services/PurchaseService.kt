package it.polito.wa2.g05.server.purchases.services

import it.polito.wa2.g05.server.purchases.dtos.PurchasedProductsDTO
import java.util.UUID

interface PurchaseService {
    fun register(token: String, id: UUID)
    fun getPurchasedProducts(token: String): List<PurchasedProductsDTO>
}