package it.polito.wa2.g05.server.authentication.utils

enum class Role(val roleName: String) {
    CUSTOMER("Customer"),
    EXPERT("Expert"),
    MANAGER("Manager")
}