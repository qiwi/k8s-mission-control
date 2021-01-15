package ru.qiwi.devops.mission.control.config.security

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import ru.qiwi.devops.mission.control.config.InMemoryAuthenticationProperties
import ru.qiwi.devops.mission.control.utils.getLogger

@Configuration
@ConditionalOnProperty("mission-control.web.auth.type", havingValue = "IN_MEMORY")
class InMemoryAuthenticationConfiguration(
    private val config: InMemoryAuthenticationProperties
) : AuthenticationConfiguration {
    private val logger = getLogger<InMemoryAuthenticationConfiguration>()

    init {
        logger.info("Using authentication manager with users stored in memory...")
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService())
    }

    @Bean
    fun userDetailsService(): InMemoryUserDetailsManager {
        val passwordEncoder = passwordEncoder()

        return InMemoryUserDetailsManager(config.users.map { user ->
            val authorities = user.roles
                .map { SimpleGrantedAuthority(it) }
                .toList()

            User(user.name, passwordEncoder.encode(user.password), authorities)
        })
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}