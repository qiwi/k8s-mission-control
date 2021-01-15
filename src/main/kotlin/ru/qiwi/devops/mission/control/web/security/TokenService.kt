package ru.qiwi.devops.mission.control.web.security

import org.springframework.security.core.Authentication
import java.time.Duration

interface TokenService {
    fun createToken(data: CreateTokenData, options: CreateTokenOptions): WebToken

    fun createToken(authentication: Authentication, options: CreateTokenOptions): WebToken

    fun signToken(token: WebToken): String

    fun verifyToken(token: String, csrfToken: String?): TokenVerificationResult

    data class CreateTokenData(
        val userName: String,
        val displayName: String,
        val roles: List<String>
    )

    data class CreateTokenOptions(
        val addCsrfToken: Boolean = false,
        val lifetime: Duration? = null,
        val unlimited: Boolean = false
    )
}