package ru.qiwi.devops.mission.control.web.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.qiwi.devops.mission.control.web.errors.ApiErrors
import ru.qiwi.devops.mission.control.web.model.ApiResponseEntity
import ru.qiwi.devops.mission.control.web.model.CreateSessionDTO
import ru.qiwi.devops.mission.control.web.model.SessionDTO
import ru.qiwi.devops.mission.control.web.model.toResponseEntity
import ru.qiwi.devops.mission.control.web.model.toSessionDTO
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.web.cookie.CookieFactory
import ru.qiwi.devops.mission.control.web.security.JWTAuthenticationToken
import ru.qiwi.devops.mission.control.web.security.TokenService
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/api/sessions")
class SessionController(
    private val authenticationManager: AuthenticationManager,
    private val tokenService: TokenService,
    private val cookieFactory: CookieFactory
) {
    private val logger = getLogger<SessionController>()
    private val empty = Empty()

    @PostMapping
    fun authenticate(
        @Valid @RequestBody
        data: CreateSessionDTO,
        response: HttpServletResponse
    ): ApiResponseEntity<SessionDTO> {
        try {
            return UsernamePasswordAuthenticationToken(data.userName, data.password)
                .let { authenticationManager.authenticate(it) }
                .let { authentication ->
                    if (authentication.isAuthenticated) {
                        logger.info("User ${data.userName} has been authenticated")

                        val setCookie = data.cookie ?: true
                        val token = tokenService.createToken(authentication, TokenService.CreateTokenOptions(
                            addCsrfToken = setCookie
                        ))
                        val signedToken = tokenService.signToken(token)

                        if (setCookie) {
                            val cookie = cookieFactory.createCookie(token, signedToken)
                            response.addCookie(cookie)
                        }

                        return token.toSessionDTO(
                            token = if (setCookie) null else signedToken
                        ).toResponseEntity()
                    } else {
                        logger.info("User credentials has been declined")
                        ApiErrors.INVALID_CREDENTIALS.toResponseEntity()
                    }
                }
        } catch (e: BadCredentialsException) {
            if (logger.isDebugEnabled) {
                logger.info("User credentials has been declined", e)
            } else {
                logger.info("User credentials has been declined: ${e.message}")
            }
            return ApiErrors.INVALID_CREDENTIALS.toResponseEntity()
        }
    }

    @GetMapping("/me")
    @PreAuthorize("permitAll")
    fun me(): ApiResponseEntity<SessionDTO> {
        val authentication = SecurityContextHolder.getContext().authentication

        return when (authentication) {
            is JWTAuthenticationToken -> authentication.token.toSessionDTO()
            else -> createAnonymousSession(authentication.name)
        }.toResponseEntity()
    }

    @DeleteMapping
    fun delete(response: HttpServletResponse): Empty {
        val authentication = SecurityContextHolder.getContext().authentication
        logger.info("User ${authentication.name} has been logged out")
        response.addCookie(cookieFactory.createEmptyCookie())
        return empty
    }

    fun createAnonymousSession(name: String): SessionDTO {
        return SessionDTO(name, name, emptyList(), null, null)
    }

    class Empty
}