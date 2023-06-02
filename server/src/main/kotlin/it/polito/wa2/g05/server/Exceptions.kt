package it.polito.wa2.g05.server

import org.springframework.validation.FieldError

class ValidationException(val errors: List<FieldError>) :
    RuntimeException("Validation errors")