package ru.qiwi.devops.mission.control.web.security

import com.auth0.jwt.algorithms.Algorithm
import java.time.Duration

interface TokenServiceConfig {
    val defaultLifetime: Duration
    val issuer: String
    val algorithm: Algorithm
}