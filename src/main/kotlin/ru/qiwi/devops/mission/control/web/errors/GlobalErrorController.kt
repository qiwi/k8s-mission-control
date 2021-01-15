package ru.qiwi.devops.mission.control.web.errors

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.WebUtils
import ru.qiwi.devops.mission.control.web.model.ApiResponseEntity
import ru.qiwi.devops.mission.control.web.model.ErrorDTO
import ru.qiwi.devops.mission.control.web.model.toResponseEntity
import javax.servlet.http.HttpServletRequest

@RestController
class GlobalErrorController : ErrorController {
    override fun getErrorPath() = "/error"

    @RequestMapping("/error")
    fun error(request: HttpServletRequest): ApiResponseEntity<out ErrorDTO> {
        val exceptionCause = request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE)
        val message = request.getAttribute(WebUtils.ERROR_MESSAGE_ATTRIBUTE) as? String
        when (exceptionCause) {
            is ApiException -> return exceptionCause.error.toResponseEntity(description = message)
            is Throwable -> throw exceptionCause
        }

        val requestedStatus = getStatus(request)
        val error = ApiErrors.DEFAULT_ERRORS[requestedStatus] ?: ApiErrors.INTERNAL_ERROR
        return error.toResponseEntity(description = message)
    }

    protected fun getStatus(request: HttpServletRequest): HttpStatus {
        try {
            val statusCode = request.getAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE) as? Int
                ?: return HttpStatus.INTERNAL_SERVER_ERROR
            return HttpStatus.valueOf(statusCode)
        } catch (_: Exception) {
            return HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}