package it.polito.wa2.g05.server.authentication.controllers

import it.polito.wa2.g05.server.ValidationException
import it.polito.wa2.g05.server.authentication.dtos.*
import it.polito.wa2.g05.server.authentication.services.AuthenticationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.ResponseStatus

@RestController
@RequestMapping("/api")
class AuthenticationController(val authenticationService: AuthenticationService) {

    // /api/anonymous/auth/login
    @PostMapping("/anonymous/auth/login")
    fun login(@RequestBody @Valid credentials: CredentialsDTO, br: BindingResult): UserDTO {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors, "Validation Errors")

        return authenticationService.login(credentials)
    }

    // /api/anonymous/auth/signup
    @PostMapping("/anonymous/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@RequestBody @Valid data: SignupProfileDTO, br: BindingResult): CreatedUserDTO {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors, "Validation Errors")
        return authenticationService.signup(data)
    }

    // /api/manager/auth/createExpert
    @PostMapping("/manager/auth/createExpert")
    @ResponseStatus(HttpStatus.CREATED)
    fun createExpert(@RequestBody @Valid data: CreateExpertDTO, br: BindingResult): CreatedUserDTO {
        if (br.hasErrors())
            throw ValidationException(br.fieldErrors, "Validation Errors")
        return authenticationService.createExpert(data)
    }

    // /api/authenticated/auth/logout
    @DeleteMapping("/authenticated/auth/logout")
    fun logout(@RequestHeader("Authorization") token: String): HttpStatusCode {
        return authenticationService.logout(token)
    }
}