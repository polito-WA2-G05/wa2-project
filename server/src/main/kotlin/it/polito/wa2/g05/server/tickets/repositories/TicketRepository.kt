package it.polito.wa2.g05.server.tickets.repositories

import it.polito.wa2.g05.server.products.entities.Product
import it.polito.wa2.g05.server.profiles.entities.Profile
import it.polito.wa2.g05.server.tickets.entities.Employee
import it.polito.wa2.g05.server.specializations.entities.Specialization
import it.polito.wa2.g05.server.tickets.entities.Ticket
import it.polito.wa2.g05.server.tickets.utils.PriorityLevel
import it.polito.wa2.g05.server.tickets.utils.TicketStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Date

interface TicketRepository : JpaRepository<Ticket, Long> {
    @Modifying
    @Query("UPDATE Ticket t SET t.status = :status, t.closedDate = :closedDate WHERE t.id = :id")
    fun updateStatus(@Param("id") id: Long, @Param("status") status: TicketStatus, @Param("closedDate") closedDate: Date? = null): Int

    @Modifying
    @Query("UPDATE Ticket t SET t.expert = NULL WHERE t.id = :id")
    fun removeExpert(@Param("id") id: Long): Int

    @Query("SELECT t.status FROM Ticket t WHERE t.id = :id")
    fun getStatus(@Param("id") id: Long): TicketStatus

    @Query("SELECT t.customer FROM Ticket t WHERE t.id = :id")
    fun getCustomer(@Param("id") id: Long): Profile

    @Query("SELECT t.expert FROM Ticket t WHERE t.id = :id")
    fun getExpert(@Param("id") id: Long): Employee?

    @Query("SELECT t.specialization FROM Ticket t WHERE t.id = :id")
    fun getSpecialization(@Param("id") id: Long): Specialization

    fun findAllByProduct(@Param("product") product: Product): List<Ticket>

    fun findAllByExpert(@Param("expert") expert: Employee): List<Ticket>

    fun findAllByProductAndExpert(@Param("product") product: Product, @Param("expert") expert: Employee) :List<Ticket>

    fun findAllByCustomer(@Param("customer") customer: Profile): List<Ticket>

    fun findAllByProductAndCustomer(@Param("product") product: Product, @Param("customer") customer: Profile) :List<Ticket>

    @Modifying
    @Query ("UPDATE Ticket t SET t.priorityLevel = :priorityLevel, t.expert = :expert WHERE t.id = :id")
    fun startTicket(@Param("id") id:Long, @Param("priorityLevel") priorityLevel: PriorityLevel, @Param("expert") expert: Employee) : Int

}

