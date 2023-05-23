package it.polito.wa2.g05.server.authentication.utils

enum class Role(val realmRole: String, val clientRole: String) {
    CUSTOMER("app_customer", "Customer"),
    EXPERT("app_expert", "Expert"),
    MANAGER("app_manager", "Manager")
}