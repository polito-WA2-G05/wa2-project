package it.polito.wa2.g05.server.profiles.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.ValidationException
import it.polito.wa2.g05.server.profiles.dtos.ProfileDTO
import it.polito.wa2.g05.server.profiles.dtos.ProfileFormDTO
import it.polito.wa2.g05.server.profiles.services.ProfileService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Observed
@RestController
@RequestMapping("/api")
class ProfileController(private val profileService: ProfileService) {

    private val log = LoggerFactory.getLogger("ProfileController")

    /* GET /api/public/profiles/me */

    @GetMapping("/customer/profiles/me")
    fun getProfile(@RequestHeader("Authorization") token: String): ProfileDTO {
        log.info("Customer get his profile")
        return profileService.getProfile(token)
    }

    /* PUT /api/customer/profiles/me */

    @PutMapping("/customer/profiles/me")
    fun updateProfile(
        @RequestHeader("Authorization") token: String,
        @RequestBody @Valid data: ProfileFormDTO,
        br: BindingResult
    ): ProfileDTO {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors)
        log.info("Customer updates his profile")
        return profileService.updateProfile(token, data)
    }
}