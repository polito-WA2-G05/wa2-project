package it.polito.wa2.g05.server.authentication.security

import it.polito.wa2.g05.server.authentication.security.jwt.JwtAuthConverter
import it.polito.wa2.g05.server.authentication.security.jwt.JwtTokenValidationFilter
import it.polito.wa2.g05.server.authentication.security.resolvers.DelegatedAccessDeniedHandler
import it.polito.wa2.g05.server.authentication.security.resolvers.DelegatedAuthenticationEntryPoint
import it.polito.wa2.g05.server.authentication.utils.Role
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private val jwtAuthConverter: JwtAuthConverter,
    private val authenticationEntryPoint: DelegatedAuthenticationEntryPoint,
    private val jwtTokenValidationFilter: JwtTokenValidationFilter,
    private val accessDeniedHandler: DelegatedAccessDeniedHandler
) {
    private var CUSTOMER: String = Role.CUSTOMER.roleName
    private var EXPERT: String = Role.EXPERT.roleName
    private var MANAGER: String = Role.MANAGER.roleName

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors().and()
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/api/anonymous/**").anonymous()
            .requestMatchers("/api/authenticated/**").authenticated()
            .requestMatchers("/api/manager/**").hasRole(MANAGER)
            .requestMatchers("/api/expert/**").hasRole(EXPERT)
            .requestMatchers("/api/customer/**").hasRole(CUSTOMER)
            .requestMatchers("/api/public/**").permitAll()
            .anyRequest().permitAll()
            .and()
            .formLogin().disable()
            .logout().disable()
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)

        http.addFilterAfter(jwtTokenValidationFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthConverter)
            .and().authenticationEntryPoint(authenticationEntryPoint)

        return http.build()
    }
}
