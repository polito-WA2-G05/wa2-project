package it.polito.wa2.g05.server.utils

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.util.UUID

@MappedSuperclass
abstract class AutoGeneratedUUIDEntity (
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID? = null
): EntityBase<UUID>()