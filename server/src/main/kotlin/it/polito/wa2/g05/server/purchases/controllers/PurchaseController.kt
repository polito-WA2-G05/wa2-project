package it.polito.wa2.g05.server.purchases.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.purchases.dtos.PurchasedProductsDTO
import it.polito.wa2.g05.server.purchases.services.PurchaseService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Observed
@RestController
@RequestMapping("/api")
class PurchaseController(private val purchaseService: PurchaseService) {

    private val log = LoggerFactory.getLogger("PurchaseController")

    // PUT /api/customer/purchases/{id}/register

    @PutMapping("/customer/purchases/{id}/register")
    fun registerPurchease(@RequestHeader("Authorization") token: String, @PathVariable id: UUID) {
        log.info("Customer registers purchase with id $id")
        purchaseService.register(token, id)
    }

    // GET /api/customer/purchases/products

    @GetMapping("/customer/purchases/products")
    fun getPurchasedProducts(@RequestHeader("Authorization") token: String): List<PurchasedProductsDTO> {
        log.info("Customer gets purchased products")
        return purchaseService.getPurchasedProducts(token)
    }
}