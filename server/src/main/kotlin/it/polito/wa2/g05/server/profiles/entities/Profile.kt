package it.polito.wa2.g05.server.profiles.entities

import it.polito.wa2.g05.server.EntityBaseUUID
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name="profiles")
class Profile(
    var name : String = "",
    var surname : String = "",
    var email : String = ""
): EntityBaseUUID()