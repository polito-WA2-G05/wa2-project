package it.polito.wa2.g05.server.authentication.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.ValidationException
import it.polito.wa2.g05.server.authentication.dtos.*
import it.polito.wa2.g05.server.authentication.services.AuthenticationService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.ResponseStatus

@Observed
@RestController
@RequestMapping("/api")
class AuthenticationController(private val authenticationService: AuthenticationService) {

    private val log = LoggerFactory.getLogger("AuthenticationController")

    /* POST /api/anonymous/auth/signup */

    @PostMapping("/anonymous/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@RequestBody @Valid data: UserFormDTO<ProfileDetailsDTO>, br: BindingResult): CreatedUserDTO {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors)
        return authenticationService.signup(data)
    }

    /* POST /api/manager/auth/createExpert */

    @PostMapping("/manager/auth/createExpert")
    @ResponseStatus(HttpStatus.CREATED)
    fun createExpert(@RequestBody @Valid data: UserFormDTO<ExpertDetailsDTO>, br: BindingResult): CreatedUserDTO {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors)
        return authenticationService.createExpert(data)
    }

    /* POST /api/anonymous/auth/login */

    @PostMapping("/anonymous/auth/login")
    fun login(@RequestBody @Valid credentials: CredentialsDTO, br: BindingResult): AuthenticatedUserDTO {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors)

        return authenticationService.login(credentials)
    }

    /* POST /api/authenticated/auth/refresh-token */

    @PostMapping("/public/auth/refresh-token")
    fun refreshToken(@RequestBody @Valid data: RefreshTokenDTO, br: BindingResult): RefreshedTokensDTO {
        if (br.hasErrors()) throw ValidationException(br.fieldErrors)
        return authenticationService.refreshToken(data)
    }

    /* DELETE /api/authenticated/auth/logout */

    @DeleteMapping("/authenticated/auth/logout")
    fun logout(@RequestHeader("Authorization") token: String): ResponseEntity<Unit> {
        return authenticationService
            .logout(token.removePrefix("Bearer").trim())
    }
}