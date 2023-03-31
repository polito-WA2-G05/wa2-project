package it.polito.wa2.g05.server.profiles

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class ProfileFormDTO(

    @field:NotNull
    @field:NotBlank
    @field:Pattern(regexp = "^\\s*[a-zA-Z]+(?:[\\s-][a-zA-Z][a-z]+)*\\s*\$", message = "Invalid format")
    var name: String?,

    @field:NotNull
    @field:NotBlank
    @field:Pattern(regexp = "^\\s*[a-zA-Z]+(?:[\\s-][a-zA-Z][a-z]+)*\\s*\$", message = "Invalid format")
    var surname: String?,

    @field:Email @field:NotNull
    var email: String?
)