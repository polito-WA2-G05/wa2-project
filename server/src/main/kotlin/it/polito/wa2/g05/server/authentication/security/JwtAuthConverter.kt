package it.polito.wa2.g05.server.authentication.security

import org.keycloak.admin.client.Keycloak
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.stream.Collectors
import java.util.stream.Stream

@Component
class JwtAuthConverter(
        @Value("\${keycloak.hostname}") private val keycloakHostname: String,
        @Value("\${keycloak.realm}")
        private val realm: String,

        @Value("\${keycloak.resource}")
        private val resource: String,
        private var properties: JwtAuthConverterProperties,
        private val keycloak: Keycloak
) :
    Converter<Jwt, AbstractAuthenticationToken> {
    private var jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(source: Jwt): AbstractAuthenticationToken? {
        val clientId = keycloak
            .realm(realm)
            .clients()
            .findByClientId(resource)
            .first().id

        val authorities = keycloak
            .realm(realm)
            .users()
            .get(source.subject)
            .roles()
            .clientLevel(clientId)
            .listAll()
            .map { SimpleGrantedAuthority("ROLE_$it") }

        return JwtAuthenticationToken(source, authorities, source.getClaim(properties.principalAttribute ?: source.subject))
    }

    private fun convertStringToJwt(accessTokenString: String): Jwt {
        val jwtDecoder: JwtDecoder = NimbusJwtDecoder.withJwkSetUri("http://localhost:8081/realms/wa2g05keycloak/protocol/openid-connect/certs").build()
        return jwtDecoder.decode(accessTokenString)
    }

    fun getRole(accessToken: String): String {
        val jwt = this.convertStringToJwt(accessToken)
        val clientAccess = jwt.getClaim<Map<*, *>>("resource_access").get("wa2g05keycloak-client") as Map<*, *>
        return (clientAccess.get("roles") as List<String>)[0]
    }

    fun getUUID(accessToken: String): UUID {
        val jwt = this.convertStringToJwt(accessToken)
        return UUID.fromString(jwt.getClaim<String>("sub"))
    }

    fun getEmail(accessToken: String): String {
        val jwt = this.convertStringToJwt(accessToken)
        return jwt.getClaim<String>("email")
    }

    fun getUsername(accessToken: String): String {
        val jwt = this.convertStringToJwt(accessToken)
        return jwt.getClaim<String>("preferred_username")
    }
}