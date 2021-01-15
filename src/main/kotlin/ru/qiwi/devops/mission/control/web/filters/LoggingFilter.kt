package ru.qiwi.devops.mission.control.web.filters

import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.UriComponentsBuilder
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.utils.isApiRequest
import javax.servlet.AsyncEvent
import javax.servlet.AsyncListener
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LoggingFilter : OncePerRequestFilter(), Filter, Ordered {
    private val log = getLogger<LoggingFilter>()

    private fun isLogging(request: HttpServletRequest) = request.isApiRequest()

    override fun getOrder() = FiltersOrdering.LOGGING

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if (!log.isInfoEnabled) {
            chain.doFilter(request, response)
            return
        }

        var isSkipping = true
        try {
            try {
                if (isLogging(request)) {
                    isSkipping = false
                    logRequest(request)
                }
            } catch (e: Throwable) {
                log.error("LoggingFilter has been finished with error", e)
            }

            chain.doFilter(request, response)
        } finally {
            if (isSkipping) {
                return
            }
            if (isAsyncStarted(request)) {
                request.asyncContext.addListener(object : AsyncListener {
                    override fun onStartAsync(event: AsyncEvent) { }

                    override fun onComplete(event: AsyncEvent) {
                        logRequestFinish(event.suppliedResponse as HttpServletResponse)
                    }

                    override fun onTimeout(event: AsyncEvent) {
                        logRequestFinish(event.suppliedResponse as HttpServletResponse)
                    }

                    override fun onError(event: AsyncEvent) {
                        logRequestFinish(event.suppliedResponse as HttpServletResponse)
                    }
                })
            } else {
                logRequestFinish(response)
            }
        }
    }

    private fun logRequest(request: ServletRequest) {
        val httpRequest = request as HttpServletRequest
        val origin = httpRequest.getHeader(HttpHeaders.ORIGIN)
        val additionalInfo = StringBuilder()
        if (origin != null) {
            val originUrl = UriComponentsBuilder.fromOriginHeader(origin).build()
            additionalInfo.append(", originUlr: ").append(originUrl)
        }

        val message = ("Incoming request {" +
            "from " + httpRequest.remoteAddr +
            ", method: " + httpRequest.method +
            ", scheme: " + httpRequest.scheme +
            ", serverName: " + httpRequest.serverName +
            ", port: " + httpRequest.serverPort +
            ", requestURI: " + httpRequest.requestURI +
            ", parameters: " + request.getParameterMap() +
            additionalInfo.toString() +
            "}")

        log.info(message)
    }

    private fun logRequestFinish(response: HttpServletResponse) {
        log.info("Server respond with status ${response.status}")
    }
}