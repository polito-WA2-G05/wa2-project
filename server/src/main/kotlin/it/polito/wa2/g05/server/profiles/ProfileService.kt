package it.polito.wa2.g05.server.profiles

interface ProfileService {
    fun getProfile(email: String): ProfileDTO
    fun createProfile(data: ProfileFormDTO): ProfileDTO
    fun updateProfile(email: String, data: ProfileFormDTO): ProfileDTO
}