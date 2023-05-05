package it.polito.wa2.g05.server.profiles

data class ProfileDTO(
    val id: Long?,
    val name: String,
    val surname: String,
    val email: String,
)

fun Profile.toDTO(): ProfileDTO {
    return ProfileDTO(getId(), name, surname, email)
}