package it.polito.wa2.g05.server.profiles

import java.util.UUID

class ProfileNotFoundException(id: String) :
    RuntimeException("Profile with id equals to $id not found")

class EmailAlreadyExistingException(message: String) :
    RuntimeException(message)