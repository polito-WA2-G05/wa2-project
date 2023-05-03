package it.polito.wa2.g05.server.tickets.repositories

import it.polito.wa2.g05.server.tickets.entities.Change
import it.polito.wa2.g05.server.tickets.entities.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param

interface ChangeRepository : JpaRepository<Change,Long> {

}