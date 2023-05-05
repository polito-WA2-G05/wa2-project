package it.polito.wa2.g05.server.products

import it.polito.wa2.g05.server.EntityBase
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name="products")
class Product (
    var ean: String = "",
    var name: String = "",
    var brand: String = ""
): EntityBase<Long>()

