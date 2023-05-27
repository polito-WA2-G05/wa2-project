package it.polito.wa2.g05.server.profiles.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.ValidationException
import it.polito.wa2.g05.server.profiles.dtos.ProfileDTO
import it.polito.wa2.g05.server.profiles.dtos.ProfileFormDTO
import it.polito.wa2.g05.server.profiles.services.ProfileService

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Observed
@RequestMapping("/api")
class ProfileController(private val profileService: ProfileService){

    private val log = LoggerFactory.getLogger("ProfileController")
    // /api/public/profiles/{email}
    @GetMapping("/public/profiles/{email}")
    fun getProfile(@PathVariable email: String): ProfileDTO {
       log.info("Getting the profile by email")
        return profileService.getProfile(email)
    }

    // /api/customer/profiles/{email}
    @PutMapping("/customer/profiles/{email}")
    fun updateProfile(@PathVariable email: String, @RequestBody @Valid data: ProfileFormDTO, br: BindingResult) : ProfileDTO {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors, "Validation errors")
        log.info("Update profile by $email")
        return profileService.updateProfile(email, data)
    }
}