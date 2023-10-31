package it.polito.wa2.g05.server.authentication.security.keycloak

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
data class KeycloakProperties(
    @Value("\${keycloak.hostname}") val hostname: String,
    @Value("\${keycloak.protocol-endpoint}") val protocolEndpoint: String,
    @Value("\${keycloak.realm}") val realm: String,
    @Value("\${keycloak.resource}") val clientId: String,
    @Value("\${keycloak.auth-server-url}") val authServerUrl: String,
    @Value("\${keycloak.credentials.secret}") val secret: String,
    @Value("\${client.port}") val redirectUriPort: String
)