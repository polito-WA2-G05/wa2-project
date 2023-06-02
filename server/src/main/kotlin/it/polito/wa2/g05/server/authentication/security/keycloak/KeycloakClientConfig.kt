package it.polito.wa2.g05.server.authentication.security.keycloak

import org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KeycloakClientConfig(private val properties: KeycloakProperties) {
    @Bean
    fun keycloak(): Keycloak {
        return KeycloakBuilder.builder()
            .grantType(CLIENT_CREDENTIALS)
            .serverUrl(properties.authServerUrl)
            .realm(properties.realm)
            .clientId(properties.clientId)
            .clientSecret(properties.secret)
            .build()
    }
}