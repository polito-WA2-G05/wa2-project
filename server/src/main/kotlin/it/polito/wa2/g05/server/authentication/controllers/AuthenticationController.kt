package it.polito.wa2.g05.server.authentication.controllers

import it.polito.wa2.g05.server.ValidationException
import it.polito.wa2.g05.server.authentication.dtos.CredentialsDTO
import it.polito.wa2.g05.server.authentication.services.AuthenticationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatusCode
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestHeader

@RestController
@RequestMapping("/api")
class AuthenticationController(val authenticationService: AuthenticationService) {

    @PostMapping("/public/auth/login")
    fun login(@RequestBody @Valid credentials: CredentialsDTO, br: BindingResult): Any {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors, "Validation Errors")

        return authenticationService.login(credentials)
    }

    @DeleteMapping("/authenticated/auth/logout")
    fun logout(@RequestHeader("Authorization") token: String): HttpStatusCode {
        return authenticationService.logout(token)
    }
}