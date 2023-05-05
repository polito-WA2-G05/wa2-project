package it.polito.wa2.g05.server.tickets.repositories

import it.polito.wa2.g05.server.tickets.entities.Employee
import it.polito.wa2.g05.server.tickets.entities.Specialization
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EmployeeRepository : JpaRepository<Employee,Long> {
    @Modifying
    @Query("UPDATE Employee e SET e.isWorkingOn = e.isWorkingOn + 1 WHERE e.id = :id")
    fun increaseIsWorkingOn(@Param("id") id: Long): Int

    @Modifying
    @Query("UPDATE Employee e SET e.isWorkingOn = e.isWorkingOn - 1 WHERE e.id = :id")
    fun decreaseIsWorkingOn(@Param("id") id: Long): Int

    fun findAllBySpecializations(@Param("specializations") specialization: Specialization): List<Employee>
}