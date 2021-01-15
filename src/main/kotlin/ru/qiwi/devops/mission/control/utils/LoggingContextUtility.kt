package ru.qiwi.devops.mission.control.utils

import org.slf4j.MDC

object LoggingContextUtility {
    private const val MDC_TRACE_ID = "traceId"
    private const val MDC_SPAN_ID = "spanId"
    private const val MDC_PARENT_SPAN_ID = "parentSpanId"
    private const val MDC_USER = "user"
    private const val MDC_USER_IS_ANON = "user_anon"

    fun putValues(vararg pairs: Pair<String, String>): AutoCloseable {
        pairs.forEach { MDC.put(it.first, it.second) }
        return AutoCloseable { pairs.forEach { MDC.remove(it.first) } }
    }

    fun putTrace(trace: TraceUtility.TraceInfo): AutoCloseable {
        return putValues(
            MDC_TRACE_ID to trace.traceId,
            MDC_SPAN_ID to trace.spanId,
            MDC_PARENT_SPAN_ID to (trace.parentSpanId ?: "")
        )
    }

    fun putUser(user: String, isAnon: Boolean): AutoCloseable {
        return putValues(
            MDC_USER to user,
            MDC_USER_IS_ANON to isAnon.toString()
        )
    }

    fun clearContext() {
        MDC.clear()
    }
}