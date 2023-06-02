package it.polito.wa2.g05.server.profiles

import java.util.UUID

class ProfileNotFoundException(email: String) :
    RuntimeException("Profile with email equals to $email not found")

class EmailAlreadyExistingException(message: String) :
    RuntimeException(message)