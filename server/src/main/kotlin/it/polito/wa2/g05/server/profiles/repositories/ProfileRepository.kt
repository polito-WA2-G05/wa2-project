package it.polito.wa2.g05.server.profiles.repositories

import it.polito.wa2.g05.server.profiles.entities.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface ProfileRepository: JpaRepository<Profile, UUID> {
    fun findByEmail(@Param("email") email: String): Optional<Profile>
}

