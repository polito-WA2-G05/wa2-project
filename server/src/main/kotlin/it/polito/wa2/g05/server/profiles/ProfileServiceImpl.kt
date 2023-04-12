package it.polito.wa2.g05.server.profiles

import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(private val profileRepository: ProfileRepository) : ProfileService {
    override fun getProfile(email: String): ProfileDTO {
        val profile = profileRepository.findByEmail(email).map { it.toDTO() }

        if(profile.isEmpty){
            throw ProfileNotFoundException("Profile not found with the $email inserted")
        }
        return profile.get()
    }

    override fun createProfile(data: ProfileFormDTO): ProfileDTO {
        if (profileRepository.findByEmail(data.email!!).isPresent)
                throw EmailAlreadyExistingException("A profile with ${data.email} as email already exists")

        return profileRepository.save(Profile(data.name!!.trim(), data.surname!!.trim(), data.email!!)).toDTO()
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