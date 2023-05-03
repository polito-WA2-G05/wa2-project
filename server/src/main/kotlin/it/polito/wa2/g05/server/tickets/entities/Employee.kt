package it.polito.wa2.g05.server.tickets.entities

import it.polito.wa2.g05.server.EntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name="employees")
class Employee (
    var email : String = "",
    var role : String = "",

    @ManyToMany
    @JoinTable(
        name = "expert_specialization",
        joinColumns = [JoinColumn(name="expert_id")],
        inverseJoinColumns = [JoinColumn(name="specs_id")])
    var specializations: MutableSet<Specialization> = mutableSetOf(),

    @Column(name = "is_working_on")
    var isWorkingOn : Int = 0,
): EntityBase<Long>()

