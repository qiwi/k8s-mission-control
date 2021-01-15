package ru.qiwi.devops.mission.control.web.security

sealed class TokenVerificationResult {
    object InvalidToken : TokenVerificationResult()

    data class VerificationFailed(val token: WebToken) : TokenVerificationResult()

    data class Expired(val token: WebToken) : TokenVerificationResult()

    data class Verified(val token: WebToken) : TokenVerificationResult()

    data class InvalidCsrfToken(val token: WebToken) : TokenVerificationResult()
}