package ru.qiwi.devops.mission.control.web.filters

import org.springframework.core.Ordered
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.utils.LoggingContextUtility
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.utils.isApiRequest
import ru.qiwi.devops.mission.control.web.cookie.CookieFactory
import ru.qiwi.devops.mission.control.web.security.JWTAuthenticationToken
import ru.qiwi.devops.mission.control.web.security.RolesService
import ru.qiwi.devops.mission.control.web.security.TokenService
import ru.qiwi.devops.mission.control.web.security.TokenVerificationResult
import ru.qiwi.devops.mission.control.web.security.WebToken
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AnonymousAuthenticationFilter(
    private val tokenService: TokenService,
    private val rolesService: RolesService,
    private val cookieFactory: CookieFactory,
    private val config: AnonymousUserConfig,
    authenticationManager: AuthenticationManager
) : BasicAuthenticationFilter(authenticationManager), Ordered {
    companion object {
        const val DEFAULT_COOKIE_NAME = "mission-control-anon-token"
    }

    override fun getOrder() = FiltersOrdering.SECURITY_ANONYMOUS

    private val log = getLogger<AnonymousAuthenticationFilter>()

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        if (!req.isApiRequest()) {
            chain.doFilter(req, res)
            return
        }

        val existsAuthentication = SecurityContextHolder.getContext().authentication
        if (existsAuthentication != null && existsAuthentication.isAuthenticated) {
            log.debug("There is another authentication ($existsAuthentication) in the context, skip anonymous authentication")
            chain.doFilter(req, res)
            return
        }

        var auth = req.extractAnonymousAuthentication()

        if (auth == null) {
            val token = tokenService.createToken(TokenService.CreateTokenData(
                userName = config.name,
                displayName = config.name,
                roles = config.roles.toList()
            ), TokenService.CreateTokenOptions(
                unlimited = true
            ))
            val signedToken = tokenService.signToken(token)

            val cookie = cookieFactory.createCookie(token, signedToken, CookieFactory.CreateCookieOptions(
                name = DEFAULT_COOKIE_NAME
            ))
            res.addCookie(cookie)

            auth = token.toAuthentication()

            log.info("New anonymous token has been created, id ${auth.token.id}, issued at ${auth.token.issuedAt}")
        } else {
            log.debug("Stored anonymous token has been found, id ${auth.token.id}, issued at ${auth.token.issuedAt}")
        }

        SecurityContextHolder.getContext().authentication = auth

        LoggingContextUtility.putUser(auth.token.id, true)

        chain.doFilter(req, res)
    }

    private fun HttpServletRequest.extractAnonymousAuthentication(): JWTAuthenticationToken? {
        return this.getCookieToken()
            ?.let { tokenService.verifyToken(it, csrfToken = null) }
            ?.let {
                when (it) {
                    is TokenVerificationResult.Verified -> it.token
                    else -> {
                        log.debug("Stored anonymous token has been found, but it's invalid or expired")
                        null
                    }
                }
            }?.let { it.toAuthentication() }
    }

    private fun WebToken.toAuthentication() = JWTAuthenticationToken(this, rolesService.mapRolesToAuthorities(this.roles))

    private fun HttpServletRequest.getCookieToken(): String? {
        return this.cookies
            ?.firstOrNull { it.name == DEFAULT_COOKIE_NAME }
            ?.value
    }

    interface AnonymousUserConfig {
        val name: String
        val roles: Iterable<String>
    }
}
