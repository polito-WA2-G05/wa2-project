package it.polito.wa2.g05.server.tickets.entities

import it.polito.wa2.g05.server.specializations.entities.Specialization
import it.polito.wa2.g05.server.utils.EntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name="employees")
class Employee (
    @Id
    override var id: UUID?,

    var username: String = "",

    @ManyToMany
    @JoinTable(
        name = "expert_specialization",
        joinColumns = [JoinColumn(name="expert_id")],
        inverseJoinColumns = [JoinColumn(name="specs_id")])
    var specializations: MutableSet<Specialization> = mutableSetOf(),

    @Column(name = "working_on")
    var workingOn : Int = 0,
): EntityBase<UUID>()

