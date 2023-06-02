package it.polito.wa2.g05.server.authentication.security.jwt

import it.polito.wa2.g05.server.authentication.security.keycloak.KeycloakService
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class JwtAuthConverter(private val keycloakService: KeycloakService) :
    Converter<Jwt, AbstractAuthenticationToken> {

    override fun convert(source: Jwt): AbstractAuthenticationToken? {
        val authorities = keycloakService
            .getUserAuthorities(source.subject)
            .map { SimpleGrantedAuthority("ROLE_$it") }

        return JwtAuthenticationToken(
            source,
            authorities,
            source.getClaim("preferred_username") ?: source.subject
        )
    }
}