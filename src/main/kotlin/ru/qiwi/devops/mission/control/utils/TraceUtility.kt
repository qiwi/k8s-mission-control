package ru.qiwi.devops.mission.control.utils

import java.util.concurrent.ThreadLocalRandom

object TraceUtility {
    fun createTrace(): TraceInfo {
        val traceId = generateTraceId()
        return TraceInfo(traceId, traceId, null)
    }

    fun createTrace(parentTraceId: String, parentSpanId: String): TraceInfo {
        val traceId = generateTraceId()
        return TraceInfo(parentTraceId, traceId, parentSpanId)
    }

    private fun generateTraceId(): String {
        return ThreadLocalRandom.current().nextHex(16)
    }

    data class TraceInfo(
        val traceId: String,
        val spanId: String,
        val parentSpanId: String?
    )
}