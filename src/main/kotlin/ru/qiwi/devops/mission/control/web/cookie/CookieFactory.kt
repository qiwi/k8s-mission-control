package ru.qiwi.devops.mission.control.web.cookie

import ru.qiwi.devops.mission.control.web.security.WebToken
import javax.servlet.http.Cookie

interface CookieFactory {
    companion object {
        const val DEFAULT_COOKIE_NAME = "mission-control-token"
    }

    fun createCookie(token: WebToken, signedToken: String, options: CreateCookieOptions = CreateCookieOptions()): Cookie

    fun createEmptyCookie(): Cookie

    data class CreateCookieOptions(
        val name: String? = null
    )
}