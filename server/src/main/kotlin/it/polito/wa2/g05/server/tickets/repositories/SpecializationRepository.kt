package it.polito.wa2.g05.server.tickets.repositories

import it.polito.wa2.g05.server.tickets.entities.Specialization
import org.springframework.data.jpa.repository.JpaRepository

interface SpecializationRepository: JpaRepository<Specialization, Long> {

}