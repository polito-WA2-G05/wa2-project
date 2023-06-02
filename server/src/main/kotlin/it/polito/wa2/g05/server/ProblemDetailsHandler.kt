package it.polito.wa2.g05.server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.authentication.UsernameOrEmailAlreadyExistsException
import it.polito.wa2.g05.server.products.ProductNotFoundException
import it.polito.wa2.g05.server.profiles.EmailAlreadyExistingException
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.tickets.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.security.access.AccessDeniedException
import javax.ws.rs.WebApplicationException

@Observed
@RestControllerAdvice
class ProblemDetailsHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger("ProblemDetailsHandler")

    /* General Exception Handlers */

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, "Failed to read request")
        return super.handleExceptionInternal(ex, problemDetail, headers, status, request)
    }

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

    @ExceptionHandler(HttpClientErrorException::class)
    fun handleHttpClientError(e: HttpClientErrorException): ProblemDetail {
        val objectMapper = jacksonObjectMapper()
        val resolvedType = objectMapper
            .typeFactory
            .constructMapType(Map::class.java, String::class.java, String::class.java)

        val responseBody: Map<String, String> = objectMapper
            .readValue(e.responseBodyAsString, resolvedType)

        return ProblemDetail.forStatusAndDetail(
            e.statusCode,
            responseBody.getOrDefault("error_description", "Http client error")
        )
    }

    @ExceptionHandler(WebApplicationException::class)
    fun handleWebApplicationError(e: WebApplicationException) =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.valueOf(e.response.status),
            e.message!!
        )


    /* Product Exception Handlers */

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFound(e: ProductNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)


    /* Profile Exception Handlers */

    @ExceptionHandler(ProfileNotFoundException::class)
    fun handleProductNotFound(e: ProfileNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(EmailAlreadyExistingException::class)
    fun handleProductNotFound(e: EmailAlreadyExistingException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

    /* Ticket Exception Handlers */

    @ExceptionHandler(TicketNotFoundException::class)
    fun handleTicketNotFound(e: TicketNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(TicketStatusNotValidException::class)
    fun handleTicketNotFound(e: TicketStatusNotValidException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.message!!)

    @ExceptionHandler(ForbiddenActionException::class)
    fun handleForbiddenAction(e: ForbiddenActionException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.message!!)

    /* Employee Exception Handlers */

    @ExceptionHandler(EmployeeNotFoundException::class)
    fun handleEmployeeNotFound(e: EmployeeNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    /* Specialization Exception Handlers */

    @ExceptionHandler(SpecializationNotFoundException::class)
    fun handleSpecializationNotFound(e: SpecializationNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    /* Authentication Exception Handlers */

    @ExceptionHandler(UsernameOrEmailAlreadyExistsException::class)
    fun handleInvalidUserCredentials(e: UsernameOrEmailAlreadyExistsException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

    @ExceptionHandler(InvalidBearerTokenException::class)
    fun handleInvalidBearerToken(e: InvalidBearerTokenException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.message!!)

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationError(e: AuthenticationException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.message!!)

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.message!!)
}

data class ValidationError(
    val field: String,
    val error: String
)