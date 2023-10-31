package it.polito.wa2.g05.server.profiles.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ProfileFormDTO(
    @field:NotBlank
    @field:Pattern(regexp = "^\\s*[a-zA-Z]+(?:[\\s-][a-zA-Z][a-z]+)*\\s*\$", message = "Invalid format")
    var name: String,

    @field:NotBlank
    @field:Pattern(regexp = "^\\s*[a-zA-Z]+(?:[\\s-][a-zA-Z][a-z]+)*\\s*\$", message = "Invalid format")
    var surname: String
)