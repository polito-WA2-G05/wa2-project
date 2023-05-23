package it.polito.wa2.g05.server.authentication.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Validated
@Configuration
@ConfigurationProperties(prefix = "jwt.auth.converter")
class JwtAuthConverterProperties {
    lateinit var resourceId: String
    lateinit var principalAttribute: String
}