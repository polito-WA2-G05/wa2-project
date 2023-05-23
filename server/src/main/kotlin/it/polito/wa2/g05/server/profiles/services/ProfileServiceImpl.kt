package it.polito.wa2.g05.server.profiles.services

import it.polito.wa2.g05.server.profiles.EmailAlreadyExistingException
import it.polito.wa2.g05.server.profiles.ProfileNotFoundException
import it.polito.wa2.g05.server.profiles.dtos.ProfileDTO
import it.polito.wa2.g05.server.profiles.dtos.ProfileFormDTO
import it.polito.wa2.g05.server.profiles.dtos.toDTO
import it.polito.wa2.g05.server.profiles.entities.Profile
import it.polito.wa2.g05.server.profiles.repositories.ProfileRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProfileServiceImpl(private val profileRepository: ProfileRepository) : ProfileService {
    override fun getProfile(email: String): ProfileDTO {
        val profile = profileRepository.findByEmail(email).map { it.toDTO() }
        if(profile.isEmpty){
            throw ProfileNotFoundException("Profile not found with the $email inserted")
        }
        return profile.get()
    }

    override fun updateProfile(email: String, data: ProfileFormDTO): ProfileDTO {
        val profile = profileRepository.findByEmail(email)
        if(profile.isEmpty){
            throw ProfileNotFoundException("Profile not found with the $email provided")
        }

        val entity = profile.get()

        entity.name = data.name!!
        entity.surname = data.surname!!
        entity.email = data.email!!

        return profileRepository.save(entity).toDTO()
    }
}