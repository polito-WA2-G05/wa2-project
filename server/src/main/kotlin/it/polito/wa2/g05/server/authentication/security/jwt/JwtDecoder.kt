package it.polito.wa2.g05.server.authentication.security.jwt

import it.polito.wa2.g05.server.authentication.security.keycloak.KeycloakProperties
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.stereotype.Component


@Component
class JwtDecoder(properties: KeycloakProperties) {

    private val jwkSetUri = "${properties.protocolEndpoint}/certs"
    private val decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()

    fun decode(token: String): Jwt =
        decoder.decode(token.removePrefix("Bearer").trim())
}