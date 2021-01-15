package ru.qiwi.devops.mission.control.config.security

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.userdetails.UserDetailsService
import ru.qiwi.devops.mission.control.utils.getLogger

@Configuration
@ConditionalOnMissingBean(LdapAuthenticationConfiguration::class, InMemoryAuthenticationConfiguration::class)
class DisabledAuthenticationConfiguration : AuthenticationConfiguration {
    private val logger = getLogger<InMemoryAuthenticationConfiguration>()

    init {
        logger.info("Starting without any authentication...")
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.parentAuthenticationManager {
            throw BadCredentialsException("Authentication is disabled")
        }
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { userName ->
            logger.warn("Can't find user by name $userName, because authentication is disabled")
            null
        }
    }
}