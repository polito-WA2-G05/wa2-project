package it.polito.wa2.g05.server.tickets.repositories

import it.polito.wa2.g05.server.specializations.entities.Specialization
import it.polito.wa2.g05.server.tickets.entities.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface EmployeeRepository : JpaRepository<Employee,UUID> {
    @Modifying
    @Query("UPDATE Employee e SET e.workingOn = e.workingOn + 1 WHERE e.id = :id")
    fun increaseIsWorkingOn(@Param("id") id: UUID): Int

    @Modifying
    @Query("UPDATE Employee e SET e.workingOn = e.workingOn - 1 WHERE e.id = :id")
    fun decreaseIsWorkingOn(@Param("id") id: UUID): Int

    fun findAllBySpecializations(@Param("specializations") specialization: Specialization): List<Employee>
}