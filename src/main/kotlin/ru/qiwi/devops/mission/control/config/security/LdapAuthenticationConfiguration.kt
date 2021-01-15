package ru.qiwi.devops.mission.control.config.security

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.ldap.core.DirContextOperations
import org.springframework.ldap.core.support.LdapContextSource
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator
import org.springframework.security.ldap.userdetails.LdapUserDetailsService
import ru.qiwi.devops.mission.control.config.LdapAuthenticationProperties
import ru.qiwi.devops.mission.control.utils.getLogger
import java.util.regex.Pattern
import javax.naming.ldap.LdapName

@Configuration
@ConditionalOnProperty("mission-control.web.auth.type", havingValue = "LDAP")
class LdapAuthenticationConfiguration(
    private val config: LdapAuthenticationProperties
) : AuthenticationConfiguration {
    private val logger = getLogger<LdapAuthenticationConfiguration>()

    init {
        logger.info("Using ldap authentication manager...")
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.ldapAuthentication()
            .rolePrefix("")
            .userSearchBase(config.searchBase)
            .userSearchFilter(config.searchFilter)
            .ldapAuthoritiesPopulator(authoritiesPopulator())
            .contextSource()
            .url(config.url)
            .managerDn(config.readerUserName)
            .managerPassword(config.readerPassword)
    }

    @Bean
    fun userDetailsService(): LdapUserDetailsService {
        val contextSource = LdapContextSource().apply {
            setUrl(config.url)
            userDn = config.readerUserName
            password = config.readerPassword
            isAnonymousReadOnly = config.readerUserName.isNullOrBlank()
            afterPropertiesSet()
        }

        return LdapUserDetailsService(
            FilterBasedLdapUserSearch(config.searchBase, config.searchFilter, contextSource),
            authoritiesPopulator()
        )
    }

    fun authoritiesPopulator(): LdapAuthoritiesPopulator {
        return MemberOfLdapAuthoritiesPopulator(config.rolePattern)
    }

    class MemberOfLdapAuthoritiesPopulator(
        private val rolePattern: Pattern?
    ) : LdapAuthoritiesPopulator {
        override fun getGrantedAuthorities(userData: DirContextOperations, username: String): List<GrantedAuthority> {
            return userData.getStringAttributes("memberOf")
                ?.map { LdapName(it).let { name -> name.getRdn(name.size() - 1) }.value as String }
                ?.filter { rolePattern?.matcher(it)?.matches() ?: true }
                ?.map { SimpleGrantedAuthority(it) }
                ?: emptyList<SimpleGrantedAuthority>()
        }
    }
}