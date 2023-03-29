package it.polito.wa2.g05.server.profiles

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="profiles")
class Profile() {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY) var id: Int = 0
    var name : String = ""
    var surname : String = ""
    var email : String = ""

    constructor(name: String, surname: String, email: String): this() {
        this.name = name
        this.surname = surname
        this.email = email
    }
}