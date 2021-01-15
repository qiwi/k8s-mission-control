package ru.qiwi.devops.mission.control.web.errors

import org.springframework.http.HttpStatus

enum class ApiErrors(
    override val code: String,
    override val httpStatus: HttpStatus
) : ApiError {
    UNAUTHORIZED("mission-control.auth.unauthorized", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("mission-control.auth.invalid-credentials", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("mission-control.auth.access-denied", HttpStatus.FORBIDDEN),
    INVALID_TOKEN("mission-control.auth.invalid-token", HttpStatus.UNAUTHORIZED),
    INVALID_CSRF_TOKEN("mission-control.auth.invalid-csrf-token", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("mission-control.auth.expired-token", HttpStatus.UNAUTHORIZED),
    INTERNAL_ERROR("mission-control.server.internal-error", HttpStatus.INTERNAL_SERVER_ERROR),
    RESOURCE_NOT_FOUND("mission-control.resource.not-found", HttpStatus.NOT_FOUND),
    CANT_DISCOVER_PODS("mission-control.pods.can-not-discover", HttpStatus.INTERNAL_SERVER_ERROR),
    CLUSTER_NOT_FOUND("mission-control.clusters.not-found", HttpStatus.NOT_FOUND),
    CLUSTER_UNAVAILABLE("mission-control.clusters.unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    BAD_REQUEST("mission-control.server.bad-request", HttpStatus.BAD_REQUEST);

    companion object {
        val DEFAULT_ERRORS = mapOf(
            HttpStatus.UNAUTHORIZED to UNAUTHORIZED,
            HttpStatus.FORBIDDEN to ACCESS_DENIED,
            HttpStatus.INTERNAL_SERVER_ERROR to INTERNAL_ERROR
        )
    }
}