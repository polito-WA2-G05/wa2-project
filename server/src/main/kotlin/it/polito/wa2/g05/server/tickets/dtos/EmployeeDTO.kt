package it.polito.wa2.g05.server.tickets.dtos

import it.polito.wa2.g05.server.tickets.entities.Employee
import java.util.UUID

data class EmployeeDTO(
    val id: UUID?,
//    val email: String,
//    val role: String,
    val workingOn: Int,
    val specializations: MutableSet<SpecializationDTO>
)

fun Employee.toDTO(): EmployeeDTO {
    return EmployeeDTO(id, workingOn, specializations.map { it.toDTO() }.toMutableSet())
}