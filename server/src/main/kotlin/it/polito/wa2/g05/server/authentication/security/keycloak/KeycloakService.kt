package it.polito.wa2.g05.server.authentication.security.keycloak

import it.polito.wa2.g05.server.authentication.dtos.RefreshTokenDTO
import it.polito.wa2.g05.server.authentication.dtos.RefreshedTokensDTO
import it.polito.wa2.g05.server.authentication.security.jwt.JwtDecoder
import it.polito.wa2.g05.server.authentication.utils.UserDetails
import it.polito.wa2.g05.server.authentication.utils.Role
import org.keycloak.OAuth2Constants.PASSWORD
import org.keycloak.OAuth2Constants.REFRESH_TOKEN
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import java.util.UUID

/**
 * Class that implements methods to ask resources to
 * the Keycloak Authentication Server.
 *
 * @param properties the keycloak properties
 * @param keycloak the keycloak client to perform admin actions
 * @param jwtDecoder the Jwt decoder to decode access tokens
 */
@Component
class KeycloakService(
    private val properties: KeycloakProperties,
    private val keycloak: Keycloak,
    private val jwtDecoder: JwtDecoder
) {
    private val restTemplate = RestTemplateBuilder().build()

    /* Private util methods */

    /**
     * Returns keycloak client UUID found by its clientId
     * set in application.properties file.
     *
     * @return a string containing the keycloak client UUID.
     */
    private fun getClientUUID(): String =
        keycloak.realm(properties.realm)
            .clients()
            .findByClientId(properties.clientId)
            .first()
            .id

    /**
     * Returns the keycloak credential representation for the password given.
     *
     * It is a util method in charge of preparing credentials to create a new user.
     *
     * @param password the password used to create the credential representation
     * @return the keycloak credential representation
     */
    private fun preparePasswordRepresentation(password: String): CredentialRepresentation {
        val credentialRepresentation = CredentialRepresentation()
        credentialRepresentation.isTemporary = false
        credentialRepresentation.type = CredentialRepresentation.PASSWORD
        credentialRepresentation.value = password
        return credentialRepresentation
    }

    /**
     * Returns the keycloak user representation with username, email and
     * credentials set. Moreover, it set the email as verified and the user
     * as enabled.
     *
     * It is a util method in charge of preparing the user representation
     * to add it into keycloak.
     *
     * @param email the email given by the user during registration
     * @param username the username given by the user during registration
     * @param credentialRepresentation the credential representation created through the password
     * given by the user during registration
     */
    private fun prepareUserRepresentation(
        email: String,
        username: String,
        credentialRepresentation: CredentialRepresentation
    ): UserRepresentation {
        val userRepresentation = UserRepresentation()
        userRepresentation.username = username
        userRepresentation.email = email
        userRepresentation.isEmailVerified = true
        userRepresentation.credentials = listOf(credentialRepresentation)
        userRepresentation.isEnabled = true
        return userRepresentation
    }

    /**
     * Find a role by name within the active keycloak client.
     *
     * @param roleName The name of the role
     * @return The keycloak role representation
     */
    private fun findRoleByName(roleName: String): RoleRepresentation =
        keycloak.realm(properties.realm)
            .clients()
            .get(this.getClientUUID())
            .roles()
            .get(roleName)
            .toRepresentation()

    /**
     * Assigns the given client role to a user.
     *
     * @param userUUID The user UUID to be assigned the role
     * @param roleRepresentation the role representation to assign
     */
    private fun assignRole(userUUID: String, roleRepresentation: RoleRepresentation) =
        keycloak.realm(properties.realm)
            .users()
            .get(userUUID)
            .roles()
            .clientLevel(this.getClientUUID())
            .add(listOf(roleRepresentation))

    /**
     * Prepares the request header with authentication to ask some resource
     * to keycloak as admin. It also set the body content type.
     *
     * It is an important util method because keycloak is set as confidential,
     * so just allowed users can perform actions.
     *
     * @return The request header with authentication set.
     */
    private fun prepareAuthenticatedHeader(): HttpHeaders {
        val headers = HttpHeaders()
        headers.setBasicAuth(properties.clientId, properties.secret)
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        return headers
    }

    /* Public service methods */

    /**
     * Gets the user authorities, so its client roles, by its UUID.
     *
     * @param userUUID the user UUID
     * @return The list of the user authorities as string
     */
    fun getUserAuthorities(userUUID: String): List<String> =
        keycloak.realm(properties.realm)
            .users()
            .get(userUUID)
            .roles()
            .clientLevel(this.getClientUUID())
            .listAll()
            .map { it.toString() }

    /**
     * Adds a new user within the keycloak client given its data and assigns the
     * given role to the new user.
     *
     * @param email The email given by the user during registration
     * @param username The username given by the user during registration
     * @param password The password given by the user durign registration
     * @param role The role to be assigned to the new user
     * @return The UUID of the new user added
     */
    fun createUser(email: String, username: String, password: String, role: Role): UUID {
        val passwordRepresentation = this.preparePasswordRepresentation(password)
        val user = this.prepareUserRepresentation(email, username, passwordRepresentation)

        val response = keycloak
            .realm(properties.realm)
            .users()
            .create(user)

        val userUUID = CreatedResponseUtil.getCreatedId(response)

        val roleRepresentation = this.findRoleByName(role.roleName)
        this.assignRole(userUUID, roleRepresentation)

        return UUID.fromString(userUUID)
    }

    /**
     * Authenticates the user given its credentials to perform login.
     *
     * @param username The username given by the user during login
     * @param password The password given by the user during login
     * @return A UserDetails instance with all the data associated to the
     * authenticated user
     */
    fun authenticateUser(username: String, password: String): UserDetails {
        val url = "${properties.protocolEndpoint}/token"
        val headers = prepareAuthenticatedHeader()

        val body = LinkedMultiValueMap(mapOf(
            "grant_type" to listOf(PASSWORD),
            "client_id" to listOf(properties.clientId),
            "username" to listOf(username),
            "password" to listOf(password)
        ))

        val request = HttpEntity(body, headers)

        val response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            object : ParameterizedTypeReference<Map<String, String>>() {}
        )

        val accessToken = response.body?.get("access_token")!!
        val refreshToken = response.body?.get("refresh_token")!!

        val user = UserDetails(jwtDecoder.decode(accessToken), refreshToken)
        user.authorities.addAll(this.getUserAuthorities(user.uuid.toString()))

        return user
    }

    /**
     * Invalidates and revokes the given access token to perform logout.
     *
     * @param token The access token to be revoked
     * @return An empty response with the Http status code
     */
    fun invalidateToken(token: String): ResponseEntity<Unit> {
        val url = "${properties.protocolEndpoint}/revoke"
        val headers = this.prepareAuthenticatedHeader()
        val body = LinkedMultiValueMap(mapOf("token" to listOf(token)))

        val request = HttpEntity(body, headers)

        restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            String::class.java
        )

        return ResponseEntity.ok().build()
    }

    /**
     * Checks if the given token is active. If an access token is not
     * active, it may be expired or revoked.
     *
     * @param token The access token to be checked
     * @return true if the token is active, false otherwise
     */
    fun isTokenRevoked(token: String): Boolean {
        val url = "${properties.protocolEndpoint}/token/introspect"
        val headers = this.prepareAuthenticatedHeader()
        val body = LinkedMultiValueMap(mapOf("token" to listOf(token)))

        val request = HttpEntity(body, headers)

        val response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            object : ParameterizedTypeReference<Map<String, Any>>() {}
        )

        val active = response.body?.get("active")!! as Boolean

        return !active
    }

    fun refreshToken(refreshToken: String): RefreshedTokensDTO {
        val url = "${properties.protocolEndpoint}/token"
        val headers = this.prepareAuthenticatedHeader()
        val body = LinkedMultiValueMap(mapOf(
            "grant_type" to listOf(REFRESH_TOKEN),
            "refresh_token" to listOf(refreshToken)
        ))

        val request = HttpEntity(body, headers)

        val response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            object : ParameterizedTypeReference<Map<String, String>>() {}
        )

        val newAccessToken = response.body?.get("access_token")!!
        val newRefreshToken = response.body?.get("refresh_token")!!

        return RefreshedTokensDTO(newAccessToken, newRefreshToken)
    }
}