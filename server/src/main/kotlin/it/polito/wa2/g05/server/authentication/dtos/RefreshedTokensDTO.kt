package it.polito.wa2.g05.server.authentication.dtos

data class RefreshedTokensDTO(
    val accessToken: String,
    val refreshToken: String
)
