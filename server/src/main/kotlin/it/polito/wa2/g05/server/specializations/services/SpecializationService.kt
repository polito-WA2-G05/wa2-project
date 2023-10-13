package it.polito.wa2.g05.server.specializations.services

import it.polito.wa2.g05.server.specializations.dtos.SpecializationDTO

interface SpecializationService {
    fun getAllSpecializations(): List<SpecializationDTO>
}