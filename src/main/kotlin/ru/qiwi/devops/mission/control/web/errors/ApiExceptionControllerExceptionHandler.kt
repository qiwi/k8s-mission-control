package ru.qiwi.devops.mission.control.web.errors

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.qiwi.devops.mission.control.web.model.ApiResponseEntity
import ru.qiwi.devops.mission.control.web.model.ErrorDTO
import ru.qiwi.devops.mission.control.web.model.toResponseEntity
import ru.qiwi.devops.mission.control.utils.getLogger

@RestControllerAdvice
class ApiExceptionControllerExceptionHandler {
    private val logger = getLogger<ApiExceptionControllerExceptionHandler>()

    @ExceptionHandler
    fun handle(e: ApiException): ApiResponseEntity<out ErrorDTO> {
        logger.error("Request has been completed with error (code=${e.error.code}, http_status=${e.error.httpStatus.value()})")
        return e.error.toResponseEntity()
    }

    @ExceptionHandler
    fun handle(e: org.springframework.security.access.AccessDeniedException): ApiResponseEntity<out ErrorDTO> {
        logger.error("Request has been rejected by security: ${e.message}")
        return ApiErrors.ACCESS_DENIED.toResponseEntity()
    }

    @ExceptionHandler
    fun handle(e: Exception): ApiResponseEntity<out ErrorDTO> {
        logger.error("An unexpected error has been occurred", e)
        return ApiErrors.INTERNAL_ERROR.toResponseEntity()
    }
}