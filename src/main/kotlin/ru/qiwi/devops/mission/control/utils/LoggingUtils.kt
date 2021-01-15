package ru.qiwi.devops.mission.control.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC

fun getLogger(name: String) = LoggerFactory.getLogger(name)!!

fun Logger.debug(producer: () -> String) {
    if (isDebugEnabled) {
        debug(producer())
    }
}

inline fun <reified T> getLogger() = LoggerFactory.getLogger(T::class.java)!!

inline fun <T> withLoggingContext(vararg map: Pair<String, String>, body: () -> T): T {
    try {
        map.forEach { MDC.put(it.first, it.second) }
        return body()
    } finally {
        map.forEach { MDC.remove(it.first) }
    }
}