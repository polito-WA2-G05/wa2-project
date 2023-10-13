package it.polito.wa2.g05.server.specializations.entities

import it.polito.wa2.g05.server.utils.SerialIdEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "specializations")
class Specialization(
    val name: String
): SerialIdEntity()