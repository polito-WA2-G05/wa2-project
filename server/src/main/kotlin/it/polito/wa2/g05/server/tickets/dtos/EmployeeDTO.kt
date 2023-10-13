package it.polito.wa2.g05.server.tickets.dtos

import it.polito.wa2.g05.server.specializations.dtos.SpecializationDTO
import it.polito.wa2.g05.server.specializations.dtos.toDTO
import it.polito.wa2.g05.server.tickets.entities.Employee

data class EmployeeDTO(
    val workingOn: Int,
    val specializations: MutableSet<SpecializationDTO>
)

fun Employee.toDTO(): EmployeeDTO {
    return EmployeeDTO(workingOn, specializations.map { it.toDTO() }.toMutableSet())
}