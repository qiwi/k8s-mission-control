package ru.qiwi.devops.mission.control.config

import com.auth0.jwt.algorithms.Algorithm
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import ru.qiwi.devops.mission.control.web.cookie.CookieFactoryImpl
import ru.qiwi.devops.mission.control.web.filters.AnonymousAuthenticationFilter
import ru.qiwi.devops.mission.control.web.security.Grant
import ru.qiwi.devops.mission.control.web.security.RolesService
import ru.qiwi.devops.mission.control.web.security.TokenServiceConfig
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties("mission-control.web")
class WebProperties(
    val auth: AuthenticationProperties = AuthenticationProperties(),
    val roles: List<RoleDefinitionConfigRecord> = emptyList(),
    val cors: CorsProperties = CorsProperties(),

    @get:Bean
    val anonymous: AnonymousProperties = AnonymousProperties(),

    @get:Bean
    val tokens: TokensProperties,

    @get:Bean
    val cookies: CookiesProperties
) {

    @Bean
    fun rolesServiceConfig() = RolesService.RolesServiceConfig(roles)

    @Bean
    @ConditionalOnProperty("mission-control.web.auth.type", havingValue = "LDAP")
    fun ldapAuthentication() = auth.ldap ?: throw IllegalStateException("mission-control.web.auth.ldap is required")

    @Bean
    @ConditionalOnProperty("mission-control.web.auth.type", havingValue = "IN_MEMORY")
    fun inMemoryAuthentication() = auth.inMemory ?: throw IllegalStateException("mission-control.web.auth.in-memory is required")
}

data class AuthenticationProperties(
    val type: AuthenticationType = AuthenticationType.DISABLED,
    val ldap: LdapAuthenticationProperties? = null,
    val inMemory: InMemoryAuthenticationProperties? = null
)

enum class AuthenticationType {
    DISABLED,
    IN_MEMORY,
    LDAP
}

class LdapAuthenticationProperties(
    val url: String,
    val searchFilter: String,
    val searchBase: String = "",
    val readerUserName: String? = null,
    val readerPassword: String? = null,
    rolePattern: String? = null
) {
    val rolePattern = rolePattern?.toPattern()
}

data class InMemoryAuthenticationProperties(
    val users: List<InMemoryUserConfigRecord>
)

data class InMemoryUserConfigRecord(
    val name: String,
    val password: String,
    val roles: List<String>
)

data class CookiesProperties(
    override val domain: String,
    override val secured: Boolean
) : CookieFactoryImpl.CookieFactoryConfig

data class TokensProperties(
    override val issuer: String,
    val secret: String,
    val defaultLifetimeSeconds: Long
) : TokenServiceConfig {
    override val defaultLifetime: Duration = Duration.ofSeconds(defaultLifetimeSeconds)
    override val algorithm: Algorithm = Algorithm.HMAC512(secret)
}

data class AnonymousProperties(
    override val name: String = "anonymous",
    override val roles: List<String> = emptyList()
) : AnonymousAuthenticationFilter.AnonymousUserConfig

data class RoleDefinitionConfigRecord(
    override val name: String,
    override val inherits: List<String> = emptyList(),
    override val grants: List<Grant> = emptyList()
) : RolesService.RoleDefinition

data class CorsProperties(
    val allowedOrigins: List<String> = emptyList()
)