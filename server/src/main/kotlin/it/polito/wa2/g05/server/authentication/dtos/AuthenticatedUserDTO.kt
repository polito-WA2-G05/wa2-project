package it.polito.wa2.g05.server.authentication.dtos

import it.polito.wa2.g05.server.authentication.utils.UserDetails

data class AuthenticatedUserDTO (
    val info: Any,
    val details: UserDetails
)
