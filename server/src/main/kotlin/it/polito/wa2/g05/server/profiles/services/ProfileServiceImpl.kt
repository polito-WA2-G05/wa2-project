package it.polito.wa2.g05.server.profiles.services

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.profiles.dtos.ProfileDTO
import it.polito.wa2.g05.server.profiles.dtos.ProfileFormDTO
import it.polito.wa2.g05.server.profiles.dtos.toDTO
import it.polito.wa2.g05.server.profiles.repositories.ProfileRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@Observed
class ProfileServiceImpl(private val profileRepository: ProfileRepository) : ProfileService {

    private val log = LoggerFactory.getLogger("ProfileServiceImpl")

    override fun getProfile(email: String): ProfileDTO =
        profileRepository.findByEmail(email).orElseThrow {
            log.error("Exception throws processing the $email in getting profile")
            throw ProfileNotFoundException(email)
        }.toDTO()

    override fun updateProfile(email: String, data: ProfileFormDTO): ProfileDTO {
        val profile = profileRepository.findByEmail(email).orElseThrow {
            log.error("Profile couldn't be updated for the $email provided")
            throw ProfileNotFoundException(email)
        }

        profile.name = data.name
        profile.surname = data.surname
        // profile.email = data.email

        return profileRepository.save(profile).toDTO()
    }
}