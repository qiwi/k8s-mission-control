package ru.qiwi.devops.mission.control.web.cookie

import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.web.security.WebToken
import java.time.Duration
import java.time.Instant
import javax.servlet.http.Cookie

@Component
class CookieFactoryImpl(private val config: CookieFactoryConfig) : CookieFactory {
    val TEN_YEARS = 60 * 60 * 24 * 365 * 10

    override fun createCookie(token: WebToken, signedToken: String, options: CookieFactory.CreateCookieOptions): Cookie {
        return Cookie(options.name ?: CookieFactory.DEFAULT_COOKIE_NAME, signedToken).apply {
            secure = config.secured
            config.domain?.let { domain = it }
            maxAge = token.expiresAt?.let { Duration.between(Instant.now(), it).seconds.toInt() } ?: TEN_YEARS
            path = "/api"
            isHttpOnly = true
        }
    }

    override fun createEmptyCookie(): Cookie {
        return Cookie(CookieFactory.DEFAULT_COOKIE_NAME, "").apply {
            secure = config.secured
            config.domain?.let { domain = it }
            maxAge = 0
            path = "/api"
            isHttpOnly = true
        }
    }

    interface CookieFactoryConfig {
        val domain: String?
        val secured: Boolean
    }
}