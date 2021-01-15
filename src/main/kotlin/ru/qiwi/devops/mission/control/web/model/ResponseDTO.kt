package ru.qiwi.devops.mission.control.web.model

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import ru.qiwi.devops.mission.control.web.errors.ApiError

typealias ApiResponseEntity<T> = ResponseEntity<ResponseDTO<T>>

sealed class ResponseDTO<out T> {
    data class Ok<out T>(
        @get:JsonValue val body: T
    ) : ResponseDTO<T>()

    data class Error<out T>(
        @get:JsonUnwrapped val body: ErrorDTO
    ) : ResponseDTO<T>()
}

fun <T> ApiError.toResponseEntity(description: String? = null, params: Map<String, String>? = null): ApiResponseEntity<T> {
    return ResponseEntity.status(this.httpStatus)
        .body(ResponseDTO.Error(this.toErrorDTO(description, params)))
}

fun ApiError.toErrorDTO(description: String? = null, params: Map<String, String>? = null) = ErrorDTO(this.code, description, params)

fun <T> T.toResponseEntity(
    contentType: MediaType? = null
): ApiResponseEntity<T> {
    val headers = HttpHeaders()

    if (contentType != null) {
        headers.contentType = contentType
    }

    return ResponseEntity(ResponseDTO.Ok(this), headers, HttpStatus.OK)
}