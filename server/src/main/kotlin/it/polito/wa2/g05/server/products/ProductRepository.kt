package it.polito.wa2.g05.server.products

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: JpaRepository<Product, String> {} //Kind of object is dealing with and which primary key.