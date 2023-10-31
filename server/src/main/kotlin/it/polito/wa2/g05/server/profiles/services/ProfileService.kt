package it.polito.wa2.g05.server.profiles.services

import it.polito.wa2.g05.server.profiles.dtos.ProfileDTO
import it.polito.wa2.g05.server.profiles.dtos.ProfileFormDTO

interface ProfileService {
    fun getProfile(token: String): ProfileDTO
    fun updateProfile(token: String, data: ProfileFormDTO): ProfileDTO
}