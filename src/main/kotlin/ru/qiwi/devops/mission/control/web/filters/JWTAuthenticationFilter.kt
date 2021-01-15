package ru.qiwi.devops.mission.control.web.filters

import org.springframework.core.Ordered
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.util.WebUtils
import ru.qiwi.devops.mission.control.utils.LoggingContextUtility
import ru.qiwi.devops.mission.control.web.errors.ApiErrors
import ru.qiwi.devops.mission.control.web.errors.ApiException
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
class JWTAuthenticationFilter(
    private val tokenService: TokenService,
    authenticationManager: AuthenticationManager,
    private val rolesService: RolesService,
    private val userDetailsService: UserDetailsService
) : BasicAuthenticationFilter(authenticationManager), Ordered {
    private val log = getLogger<JWTAuthenticationFilter>()

    override fun getOrder() = FiltersOrdering.SECURITY_JWT

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        if (!req.isApiRequest()) {
            chain.doFilter(req, res)
            return
        }

        try {
            getAuthentication(req)
                ?.apply {
                    SecurityContextHolder.getContext().authentication = this

                    LoggingContextUtility.putUser(this.name, false)
                }

            chain.doFilter(req, res)
        } catch (e: ApiException) {
            SecurityContextHolder.clearContext()
            req.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, e)
            req.getRequestDispatcher("/error").forward(req, res)
        }
    }

    private fun getAuthentication(req: HttpServletRequest): Authentication? {
        return req.getToken()
            ?.let {
                val csrf = req.getCsrfToken()
                log.debug("Token has been received: $it, csrf=$csrf")
                tokenService.verifyToken(it, csrf)
            }
            ?.let {
                when (it) {
                    is TokenVerificationResult.InvalidToken -> {
                        log.warn("User is not authenticated: can't parse jwt token")
                        throw ApiException(ApiErrors.INVALID_TOKEN, "Invalid token has been received")
                    }
                    is TokenVerificationResult.InvalidCsrfToken -> {
                        log.warn("User ${it.token.userName} is not authenticated: invalid csrf")
                        throw ApiException(ApiErrors.INVALID_CSRF_TOKEN, "Token with invalid csrf token has been received")
                    }
                    is TokenVerificationResult.Expired -> {
                        log.warn("User ${it.token.userName} is not authenticated: token is expired")
                        throw ApiException(ApiErrors.EXPIRED_TOKEN, "Expired token has been received")
                    }
                    is TokenVerificationResult.VerificationFailed -> {
                        log.warn("User ${it.token.userName} is not authenticated: invalid jwt signature")
                        throw ApiException(ApiErrors.INVALID_TOKEN, "Invalid token has been received")
                    }
                    is TokenVerificationResult.Verified -> it.token
                }
            }
            ?.apply { checkToken(this) }
            ?.let { token ->
                val grants = rolesService.mapRolesToAuthorities(token.roles)
                if (log.isDebugEnabled) {
                    log.info("User ${token.userName} has been authenticated, token=$token, grants=$grants")
                } else {
                    log.info("User ${token.userName} has been authenticated, roles=${token.roles}, grants=$grants")
                }
                JWTAuthenticationToken(token, grants)
            }
    }

    // It checks that user still exists and isn't blocked.
    // Otherwise, it throws exception (401 UNAUTHORIZED)
    private fun checkToken(token: WebToken) {
        try {
            userDetailsService.loadUserByUsername(token.userName)
                ?: throw ApiException(ApiErrors.INVALID_TOKEN, "User is blocked")
        } catch (e: UsernameNotFoundException) {
            throw ApiException(ApiErrors.INVALID_TOKEN, "User is blocked", e)
        }
    }

    private fun HttpServletRequest.getToken(): String? {
        return this.getHeaderToken() ?: this.getCookieToken()
    }

    private fun HttpServletRequest.getCookieToken(): String? {
        return this.cookies
            ?.firstOrNull { it.name == CookieFactory.DEFAULT_COOKIE_NAME }
            ?.value
    }

    private fun HttpServletRequest.getHeaderToken(): String? {
        return this.getHeaders("Authorization").asSequence()
            .map { it.split(" ", limit = 2) }
            .singleOrNull { it.size == 2 && it[0] == "Bearer" }
            ?.get(1)
    }

    private fun HttpServletRequest.getCsrfToken(): String? {
        return this.getHeaders("X-CSRF-TOKEN").asSequence().firstOrNull()
    }
}