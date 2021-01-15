package ru.qiwi.devops.mission.control.web.security

import java.time.Instant

data class WebToken(
    val id: String,
    val userName: String,
    val displayName: String,
    val roles: List<String>,
    val expiresAt: Instant?,
    val issuedAt: Instant,
    val csrfToken: String?
)