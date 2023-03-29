package it.polito.wa2.g05.server.profiles

import it.polito.wa2.g05.server.ValidationException

import jakarta.validation.Valid
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
@RequestMapping("/api/profiles")
class ProfileController(private val profileService: ProfileService ){

    // /api/profiles/{email}
    @GetMapping("/{email}")
    fun getProfile(@PathVariable email: String): ProfileDTO {
        return profileService.getProfile(email)
    }

    // api/profiles
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProfile(@RequestBody @Valid data: CreateProfileFormDTO, br: BindingResult) : ProfileDTO {
        if (br.hasErrors()) throw ValidationException(br.fieldErrors, "Validation errors")
        return profileService.createProfile(data)
    }

    // /api/profiles/{email}
    @PutMapping("/{email}")
    fun updateProfile(@PathVariable email: String, @RequestBody @Valid data: UpdateProfileFormDTO, br: BindingResult) : ProfileDTO {
        if (br.hasErrors()) throw ValidationException(br.fieldErrors, "Validation errors")
        return profileService.updateProfile(email, data)
    }
}