package it.polito.wa2.g05.server.profiles

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

data class CreateProfileFormDTO(
    @field:NotNull var name: String?,
    @field:NotNull var surname: String?,
    @field:Email @field:NotNull var email: String?
)

data class UpdateProfileFormDTO(
    var name: String?,
    var surname: String?,
    @field:Email var email: String?
)
