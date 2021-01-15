package ru.qiwi.devops.mission.control.web.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class JWTAuthenticationToken(
    val token: WebToken,
    authorities: List<GrantedAuthority>
) : AbstractAuthenticationToken(authorities) {
    init {
        isAuthenticated = true
    }

    override fun getCredentials() = null

    override fun getPrincipal() = User(token.userName, "", authorities)
}
