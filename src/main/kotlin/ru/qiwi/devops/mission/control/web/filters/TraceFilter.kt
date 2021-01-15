package ru.qiwi.devops.mission.control.web.filters

import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.qiwi.devops.mission.control.utils.LoggingContextUtility
import ru.qiwi.devops.mission.control.utils.TraceUtility
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TraceFilter : OncePerRequestFilter(), Ordered {
    companion object {
        private const val HEADER_TRACE_ID = "X-B3-TraceId"
        private const val HEADER_SPAN_ID = "X-B3-SpanId"
        private const val HEADER_PARENT_SPAN_ID = "X-B3-ParentSpanId"
    }

    override fun getOrder() = FiltersOrdering.Trace

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, filterChain: FilterChain) {
        try {
            val trace = req.getTrace()
            LoggingContextUtility.putTrace(trace)

            res.addHeader(HEADER_TRACE_ID, trace.traceId)
            res.addHeader(HEADER_SPAN_ID, trace.spanId)
            res.addHeader(HEADER_PARENT_SPAN_ID, trace.parentSpanId)

            filterChain.doFilter(req, res)
        } finally {
            LoggingContextUtility.clearContext()
        }
    }

    private fun HttpServletRequest.getTrace(): TraceUtility.TraceInfo {
        val parentTraceId = this.getHeader(HEADER_TRACE_ID)
        val parentSpanId = this.getHeader(HEADER_SPAN_ID)

        val trace = if (parentTraceId != null && parentSpanId != null) {
            TraceUtility.createTrace(parentTraceId, parentSpanId)
        } else {
            TraceUtility.createTrace()
        }
        return trace
    }
}