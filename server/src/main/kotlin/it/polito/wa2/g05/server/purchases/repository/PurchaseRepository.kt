package it.polito.wa2.g05.server.purchases.repository

import it.polito.wa2.g05.server.profiles.entities.Profile
import it.polito.wa2.g05.server.purchases.entities.Purchase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface PurchaseRepository: JpaRepository<Purchase, UUID> {
    @Modifying
    @Query("UPDATE Purchase p SET p.profile = :profile WHERE p.id = :id")
    fun registerProfile(@Param("id") id: UUID, @Param("profile") profile: Profile): Int

    fun findAllByProfile(@Param("profile") profile: Profile): List<Purchase>
}