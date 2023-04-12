package it.polito.wa2.g05.server.profiles

data class ProfileDTO(
    val id: Int,
    val name: String,
    val surname: String,
    val email: String,
)

fun Profile.toDTO(): ProfileDTO {
    return ProfileDTO(id, name, surname, email)
}