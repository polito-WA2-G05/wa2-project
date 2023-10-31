package it.polito.wa2.g05.server.profiles.services

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.authentication.security.jwt.JwtDecoder
import it.polito.wa2.g05.server.authentication.utils.UserDetails
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.profiles.dtos.ProfileDTO
import it.polito.wa2.g05.server.profiles.dtos.ProfileFormDTO
import it.polito.wa2.g05.server.profiles.dtos.toDTO
import it.polito.wa2.g05.server.profiles.repositories.ProfileRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@Observed
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val jwtDecoder: JwtDecoder
) : ProfileService {

    private val log = LoggerFactory.getLogger("ProfileServiceImpl")

    override fun getProfile(token: String): ProfileDTO {
        val customerId = UserDetails(jwtDecoder.decode(token)).uuid

        return profileRepository.findById(customerId).orElseThrow {
            log.error("Profile not found")
            throw ProfileNotFoundException(customerId.toString())
        }.toDTO()
    }

    override fun updateProfile(token: String, data: ProfileFormDTO): ProfileDTO {
        val customerId = UserDetails(jwtDecoder.decode(token)).uuid

        val profile = profileRepository.findById(customerId).orElseThrow {
            log.error("Profile not found")
            throw ProfileNotFoundException(customerId.toString())
        }

        profile.name = data.name
        profile.surname = data.surname

        return profileRepository.save(profile).toDTO()
    }
}