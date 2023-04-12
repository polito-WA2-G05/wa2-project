package it.polito.wa2.g05.server.profiles

class ProfileNotFoundException(message: String): RuntimeException(message)
class EmailAlreadyExistingException(message: String): RuntimeException(message)