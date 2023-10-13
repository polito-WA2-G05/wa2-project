package it.polito.wa2.g05.server.specializations.repositories

import it.polito.wa2.g05.server.specializations.entities.Specialization
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpecializationRepository: JpaRepository<Specialization, Long> {
}