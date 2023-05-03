package it.polito.wa2.g05.server.profiles

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProfileRepository: JpaRepository<Profile, Long> {
    fun findByEmail(@Param("email") email: String): Optional<Profile>
}

