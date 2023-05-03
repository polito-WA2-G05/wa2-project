package it.polito.wa2.g05.server.tickets.dtos

import it.polito.wa2.g05.server.tickets.entities.Specialization

data class SpecializationDTO(
    val id: Long?,
    val name: String,
)

fun Specialization.toDTO(): SpecializationDTO {
    return SpecializationDTO(getId(), name)
}