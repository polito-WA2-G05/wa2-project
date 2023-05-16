package it.polito.wa2.g05.server.profiles.entities

import it.polito.wa2.g05.server.EntityBase
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name="profiles")
class Profile(
    @Id
    var id: UUID? = null,
    var name : String = "",
    var surname : String = "",
    var email : String = ""
)