package it.polito.wa2.g05.server.specializations.services

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.specializations.dtos.SpecializationDTO
import it.polito.wa2.g05.server.specializations.dtos.toDTO
import it.polito.wa2.g05.server.specializations.repositories.SpecializationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Observed
@Service
class SpecializationServiceImpl(private val specializationRepository: SpecializationRepository): SpecializationService {

    override fun getAllSpecializations(): List<SpecializationDTO> =
        specializationRepository.findAll().map { it.toDTO() }
}