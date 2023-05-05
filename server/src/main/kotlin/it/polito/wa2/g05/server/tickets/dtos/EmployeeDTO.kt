package it.polito.wa2.g05.server.tickets.dtos

import it.polito.wa2.g05.server.tickets.entities.Employee

data class EmployeeDTO(
    val id: Long?,
    val email: String,
    val role: String,
    val isWorkingOn: Int,
    val specializations: MutableSet<SpecializationDTO>
)

fun Employee.toDTO(): EmployeeDTO {
    return EmployeeDTO(getId(), email, role, isWorkingOn, specializations.map { it.toDTO() }.toMutableSet())
}