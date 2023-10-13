package it.polito.wa2.g05.server.specializations.dtos

import it.polito.wa2.g05.server.specializations.entities.Specialization

data class SpecializationDTO(
    val id: Long?,
    val name: String
)

fun Specialization.toDTO() : SpecializationDTO{
    return SpecializationDTO(id, name)
}