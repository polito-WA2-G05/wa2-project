package it.polito.wa2.g05.server.profiles.dtos

import it.polito.wa2.g05.server.profiles.entities.Profile
import java.util.UUID

data class ProfileDTO(
    val id: UUID?,
    val name: String,
    val surname: String,
    val email: String,
)

fun Profile.toDTO(): ProfileDTO {
    return ProfileDTO(id, name, surname, email)
}