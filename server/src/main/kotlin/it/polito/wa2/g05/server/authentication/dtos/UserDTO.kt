package it.polito.wa2.g05.server.authentication.dtos

data class UserDTO(
    val accessToken: String,
    val email: String,
    val username: String,
    val name: String?,
    val surname: String?,
    val workingOn: Int?,
)
