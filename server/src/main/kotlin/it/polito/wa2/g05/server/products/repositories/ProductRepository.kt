package it.polito.wa2.g05.server.products.repositories

import it.polito.wa2.g05.server.products.entities.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductRepository: JpaRepository<Product, Long> {
    fun findByEan(@Param("ean") ean: String): Optional<Product>
}