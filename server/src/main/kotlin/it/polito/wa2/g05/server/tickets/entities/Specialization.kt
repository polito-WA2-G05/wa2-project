package it.polito.wa2.g05.server.tickets.entities

import it.polito.wa2.g05.server.EntityBase
import jakarta.persistence.*

@Entity
@Table(name="specializations")
class Specialization(
    var name: String = "",
): EntityBase<Long>()

