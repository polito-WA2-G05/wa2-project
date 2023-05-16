package it.polito.wa2.g05.server.profiles.services

import it.polito.wa2.g05.server.profiles.dtos.ProfileDTO
import it.polito.wa2.g05.server.profiles.dtos.ProfileFormDTO

interface ProfileService {
    fun getProfile(email: String): ProfileDTO
    fun createProfile(data: ProfileFormDTO): ProfileDTO
    fun updateProfile(email: String, data: ProfileFormDTO): ProfileDTO
}