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

    override fun createProfile(data: CreateProfileFormDTO): ProfileDTO {
        if (profileRepository.findByEmail(data.email!!).isPresent)
                throw EmailAlreadyExistingException("A profile with ${data.email} as email already exists")

        return profileRepository.save(Profile(data.name!!, data.surname!!, data.email!!)).toDTO()
    }

    override fun updateProfile(email: String, data: UpdateProfileFormDTO): ProfileDTO {
        val profile = profileRepository.findByEmail(email)

        if(profile.isEmpty){
            throw ProfileNotFoundException("Profile not found with the $email inserted")
        }

        val entity = profile.get()

        if (!data.name.isNullOrEmpty()){ entity.name = data.name!! }

        if (!data.surname.isNullOrEmpty()){ entity.surname = data.surname!! }

        if (!data.email.isNullOrEmpty()){ entity.email = data.email!! }


        /*
        val myString: String? = null
        val fallbackString = "valore di fallback"
        entity.name = data.name?.let { it } ?:entity.name
        */

        return profileRepository.save(entity).toDTO()
    }
}