package it.polito.wa2.g05.server.purchases.services

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.authentication.security.jwt.JwtDecoder
import it.polito.wa2.g05.server.authentication.utils.UserDetails
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.profiles.repositories.ProfileRepository
import it.polito.wa2.g05.server.purchases.PurchaseAlreadyRegistered
import it.polito.wa2.g05.server.purchases.PurchaseNotFoundException
import it.polito.wa2.g05.server.purchases.PurchaseRegistrationFailed
import it.polito.wa2.g05.server.purchases.dtos.PurchasedProductsDTO
import it.polito.wa2.g05.server.purchases.dtos.toDTO
import it.polito.wa2.g05.server.purchases.repository.PurchaseRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Service
import java.util.UUID

@Observed
@Service
class PurchaseServiceImpl(
    private val purchaseRepository: PurchaseRepository,
    private val profileRepository: ProfileRepository,
    private val jwtDecoder: JwtDecoder
) : PurchaseService {

    private val log = LoggerFactory.getLogger("PurchaseServiceImpl")

    @Transactional
    override fun register(token: String, id: UUID) {
        val purchase = purchaseRepository.findById(id).orElseThrow {
            log.error("Purchase $id not found")
            throw PurchaseNotFoundException(id)
        }

        if (purchase.profile != null) {
            log.error("Purchase $id already registered")
            throw PurchaseAlreadyRegistered(id)
        }

        val customerId = UserDetails(jwtDecoder.decode(token)).uuid

        val customer = profileRepository.findById(customerId).orElseThrow {
            log.error("Profile $customerId not found")
            throw ProfileNotFoundException(customerId.toString())
        }

        if (purchaseRepository.registerProfile(id, customer) == 0) {
            log.error("Purchase $id registration failed")
            throw PurchaseRegistrationFailed(id)
        }
    }

    override fun getPurchasedProducts(token: String): List<PurchasedProductsDTO> {
        val customerId = UserDetails(jwtDecoder.decode(token)).uuid

        val customer = profileRepository.findById(customerId).orElseThrow {
            log.error("Profile $customerId not found")
            throw ProfileNotFoundException(customerId.toString())
        }

        return purchaseRepository.findAllByProfile(customer).map { it.toDTO() }
    }
}