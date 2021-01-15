package ru.qiwi.devops.mission.control.web.filters

import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.HandlerMapping
import ru.qiwi.devops.mission.control.utils.LoggingContextUtility
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RouteDetailsLoggingInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        (handler as? HandlerMethod)
            ?.let { h ->
                LoggingContextUtility.putValues(
                    "request.controller" to h.method.declaringClass.simpleName,
                    "request.action" to h.method.name
                )
            }

        request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)
            ?.let { it as HashMap<String, String> }
            ?.entries
            ?.map { "request.param.${it.key}" to it.value }
            ?.let { LoggingContextUtility.putValues(*it.toTypedArray()) }

        return true
    }
}