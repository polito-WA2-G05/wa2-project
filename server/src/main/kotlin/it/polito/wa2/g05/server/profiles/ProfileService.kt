package it.polito.wa2.g05.server.profiles

interface ProfileService {
    fun getProfile(email: String): ProfileDTO
    fun createProfile(data: CreateProfileFormDTO): ProfileDTO
    fun updateProfile(email: String, data: UpdateProfileFormDTO): ProfileDTO
}