package it.polito.wa2.g05.server.authentication.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
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
class JwtAuthConverter(private var properties: JwtAuthConverterProperties) :
    Converter<Jwt, AbstractAuthenticationToken> {
    private var jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities: Collection<GrantedAuthority> = Stream.concat(
            jwtGrantedAuthoritiesConverter.convert(jwt)?.stream(),
            this.extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet())
        return JwtAuthenticationToken(jwt, authorities, this.getPrincipalClaimName(jwt))
    }

    private fun getPrincipalClaimName(jwt: Jwt): String {
        var claimName: String = JwtClaimNames.SUB
        claimName = properties.principalAttribute
        return jwt.getClaim(claimName)
    }

    private fun extractResourceRoles(jwt: Jwt): Collection<GrantedAuthority> {
        val resourceAccess = jwt.getClaim<Map<String, Any>>("resource_access")
        val resource = resourceAccess?.get(properties.resourceId) as? Map<*, *>
        val resourceRoles = resource?.get("roles") as? Collection<*>

        return resourceRoles?.stream()?.map { role -> SimpleGrantedAuthority("ROLE_$role") }
            ?.collect(Collectors.toSet()) ?: emptySet()
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