package it.polito.wa2.g05.server.authentication.security.jwt

import it.polito.wa2.g05.server.authentication.security.keycloak.KeycloakService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class JwtTokenValidationFilter(
    private val keycloakService: KeycloakService,
    private val jwtDecoder: JwtDecoder,
    @Qualifier("handlerExceptionResolver") private val resolver: HandlerExceptionResolver
) : OncePerRequestFilter() {

    private fun extractToken(request: HttpServletRequest): String? {
        val token = request.getHeader("Authorization")
        if (token.isNullOrEmpty()) return null
        return token.removePrefix("Bearer").trim()
    }

    private fun createAuthentication(token: String): Authentication =
        JwtAuthenticationToken(jwtDecoder.decode(token))

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
            extractToken(request)?.let {
                if (!keycloakService.isTokenRevoked(it)) {
                    val authentication = createAuthentication(it)
                    SecurityContextHolder.getContext().authentication = authentication
                } else {
                    resolver.resolveException(
                        request,
                        response,
                        null,
                        InvalidBearerTokenException("Invalid token: it has been revoked or expired")
                    )
                    return
                }
            }

        filterChain.doFilter(request, response)
    }
}