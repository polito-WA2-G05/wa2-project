package it.polito.wa2.g05.server.utils

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class SerialIdEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null
): EntityBase<Long>()
