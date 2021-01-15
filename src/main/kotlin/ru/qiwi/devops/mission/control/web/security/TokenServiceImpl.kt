package ru.qiwi.devops.mission.control.web.security

import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.AlgorithmMismatchException
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.ldap.userdetails.Person
import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.utils.nextHex
import java.lang.IllegalStateException
import java.security.SecureRandom
import java.time.Instant
import java.util.Date

@Component
class TokenServiceImpl(
    private val config: TokenServiceConfig
) : TokenService {
    private val logger = getLogger<TokenServiceImpl>()
    private val random = SecureRandom()
    private val verifier = JWT.require(config.algorithm).withIssuer(config.issuer).build()

    override fun createToken(authentication: Authentication, options: TokenService.CreateTokenOptions): WebToken {
        val names = when (val principal = authentication.principal) {
            is Person -> Pair(principal.username, "${principal.sn} ${principal.givenName}")
            is UserDetails -> Pair(principal.username, principal.username)
            is String -> Pair(principal, principal)
            else -> throw IllegalStateException("Can't fetch username from ${principal.javaClass.simpleName}")
        }

        return createToken(TokenService.CreateTokenData(
            userName = names.first,
            displayName = names.second,
            roles = authentication.authorities.mapNotNull { (it as? SimpleGrantedAuthority)?.authority }
        ), options)
    }

    override fun createToken(data: TokenService.CreateTokenData, options: TokenService.CreateTokenOptions): WebToken {
        val id = random.nextHex(16)
        val issuedAt = Instant.now()

        return WebToken(
            id = id,
            userName = data.userName,
            displayName = data.displayName,
            roles = data.roles,
            expiresAt = if (!options.unlimited) issuedAt + (options.lifetime ?: config.defaultLifetime) else null,
            issuedAt = issuedAt,
            csrfToken = if (options.addCsrfToken) generateCsrfToken() else null
        )
    }

    override fun signToken(token: WebToken): String {
        logger.info("Signing token $token")

        return JWT.create()
            .withJWTId(token.id)
            .withSubject(token.userName)
            .withClaim("disp", token.displayName)
            .withClaim("rol", token.roles)
            .apply { token.csrfToken?.let { withClaim("csrf", it) } }
            .apply { token.expiresAt?.let { withExpiresAt(Date.from(it)) } }
            .withIssuedAt(Date.from(token.issuedAt))
            .withIssuer(config.issuer)
            .sign(config.algorithm)
    }

    override fun verifyToken(token: String, csrfToken: String?): TokenVerificationResult {
        logger.debug("Trying to parse token...")

        val decoded = try {
            JWT.decode(token)
        } catch (e: JWTDecodeException) {
            logger.debug("Can't decode token", e)
            return TokenVerificationResult.InvalidToken
        }

        val parsedToken = try {
            parse(decoded)
        } catch (e: Throwable) {
            logger.debug("Can't fetch principal from token", e)
            return TokenVerificationResult.InvalidToken
        }

        logger.debug("Token has been decoded: $parsedToken")
        logger.debug("Trying to verify token...")

        if (!parsedToken.csrfToken.isNullOrEmpty() && parsedToken.csrfToken != csrfToken) {
            logger.warn("Can't verify token: invalid csrf token (jwt=${parsedToken.csrfToken}, header=$csrfToken)")
            return TokenVerificationResult.InvalidCsrfToken(parsedToken)
        }

        try {
            verifier.verify(decoded)
        } catch (e: AlgorithmMismatchException) {
            logVerificationException(e)
            return TokenVerificationResult.VerificationFailed(parsedToken)
        } catch (e: SignatureVerificationException) {
            logVerificationException(e)
            return TokenVerificationResult.VerificationFailed(parsedToken)
        } catch (e: TokenExpiredException) {
            logVerificationException(e)
            return TokenVerificationResult.Expired(parsedToken)
        } catch (e: InvalidClaimException) {
            logVerificationException(e)
            return TokenVerificationResult.VerificationFailed(parsedToken)
        }

        logger.debug("Token has been verified successfully")
        return TokenVerificationResult.Verified(parsedToken)
    }

    private fun parse(jwt: DecodedJWT): WebToken {
        return WebToken(
            id = jwt.id ?: throw IllegalArgumentException("id is required"),
            userName = jwt.subject ?: throw IllegalArgumentException("subject is required"),
            displayName = jwt.getClaim("disp").asString()
                ?: throw IllegalArgumentException("disp claim is required"),
            roles = jwt.getClaim("rol").asList(String::class.java)
                ?: throw IllegalArgumentException("rol claim is required"),
            expiresAt = jwt.expiresAt?.toInstant(),
            issuedAt = jwt.issuedAt.toInstant(),
            csrfToken = jwt.getClaim("csrf").asString()
        )
    }

    private fun logVerificationException(e: Throwable) {
        if (logger.isDebugEnabled) {
            logger.debug("Can't verify token", e)
        } else {
            logger.warn("Can't verify token: ${e.javaClass.simpleName} ${e.message}")
        }
    }

    private fun generateCsrfToken(): String {
        return random.nextHex(16)
    }
}