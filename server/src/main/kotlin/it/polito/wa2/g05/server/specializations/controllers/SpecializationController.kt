package it.polito.wa2.g05.server.specializations.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.specializations.dtos.SpecializationDTO
import it.polito.wa2.g05.server.specializations.services.SpecializationService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Observed
@RestController
@RequestMapping("/api/authenticated/specializations")
class SpecializationController(private val specializationService: SpecializationService) {
    
    private val log = LoggerFactory.getLogger("SpecializationController")

    /* GET /api/authenticated/specializations */

    @GetMapping
    fun getAllSpecializations(): List<SpecializationDTO>{
        log.info("Getting the list of all specializations")
        return specializationService.getAllSpecializations()
    }
}