package it.polito.wa2.g05.server.authentication.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler

class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        response?.status = HttpServletResponse.SC_UNAUTHORIZED
        response?.writer?.write("You are not authenticated, please perform login first")
    }

}

class CustomAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: AccessDeniedException?
    ) {
        response?.status = HttpServletResponse.SC_FORBIDDEN
        response?.writer?.write("You are not allowed to perform this action")
    }

}

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class WebSecurityConfig(val jwtAuthConverter: JwtAuthConverter) {
    private var CUSTOMER: String = "Customer"
    private var EXPERT: String = "Expert"
    private var MANAGER: String = "Manager"

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors().and()
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/api/public/**").permitAll()
            .requestMatchers("/api/authenticated/**").authenticated()
            .requestMatchers("/api/manager/**").hasRole(MANAGER)
            .requestMatchers("/api/expert/**").hasRole(EXPERT)
            .requestMatchers("/api/customer/**").hasRole(CUSTOMER)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .formLogin().disable()
            .logout().disable()
            .exceptionHandling()
            .authenticationEntryPoint(CustomAuthenticationEntryPoint())
            .accessDeniedHandler(CustomAccessDeniedHandler())
            .and()
            .oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthConverter)

        return http.build()
    }
}
