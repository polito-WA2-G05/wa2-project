package it.polito.wa2.g05.server

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.authentication.InvalidUserCredentialsException
import it.polito.wa2.g05.server.authentication.UsernameOrEmailAlreadyExistsException
import it.polito.wa2.g05.server.products.ProductNotFoundException
import it.polito.wa2.g05.server.profiles.EmailAlreadyExistingException
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.tickets.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Observed
@RestControllerAdvice
class ProblemDetailsHandler: ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger("ProblemDetailsHandler")

    // General Exception Handlers

    @ExceptionHandler(ValidationException::class)
    fun handleValidationErrors(e: ValidationException): ProblemDetail {
        log.error("Validation error")

        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

        val validationErrors = e.errors.map {
            ValidationError(it.field, it.defaultMessage.orEmpty())
        }

        problemDetail.setProperty("errors", validationErrors)
        return problemDetail
    }

    // Product Exception Handlers

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFound(e: ProductNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    // Profile Exception Handlers

    @ExceptionHandler(ProfileNotFoundException::class)
    fun handleProductNotFound(e: ProfileNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(EmailAlreadyExistingException::class)
    fun handleProductNotFound(e: EmailAlreadyExistingException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

    // Ticket Exception Handlers

    @ExceptionHandler(TicketNotFoundException::class)
    fun handleTicketNotFound(e: TicketNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(TicketStatusNotValidException::class)
    fun handleTicketNotFound(e: TicketStatusNotValidException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    @ExceptionHandler(ForbiddenActionException::class)
    fun handleForbiddenAction(e: ForbiddenActionException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.message!!)

    // Employee Exception Handlers

    @ExceptionHandler(EmployeeNotFoundException::class)
    fun handleEmployeeNotFound(e: EmployeeNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    // Specialization Exception Handlers

    @ExceptionHandler(SpecializationNotFoundException::class)
    fun handleSpecializationNotFound(e: SpecializationNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    // Authentication Exception Handlers

    @ExceptionHandler(InvalidUserCredentialsException::class)
    fun handleInvalidUserCredentials(e: InvalidUserCredentialsException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.message!!)

    @ExceptionHandler(UsernameOrEmailAlreadyExistsException::class)
    fun handleInvalidUserCredentials(e: UsernameOrEmailAlreadyExistsException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)
}

data class ValidationError(
    val field: String,
    val error: String
)