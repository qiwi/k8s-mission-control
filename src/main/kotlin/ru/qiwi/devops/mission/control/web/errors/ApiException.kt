package ru.qiwi.devops.mission.control.web.errors

class ApiException(
    val error: ApiError,
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)