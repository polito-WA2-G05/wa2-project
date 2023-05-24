package it.polito.wa2.g05.server.products.entities

import it.polito.wa2.g05.server.utils.SerialIdEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name="products")
class Product (
    var ean: String = "",
    var name: String = "",
    var brand: String = ""
): SerialIdEntity()

