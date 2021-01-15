package ru.qiwi.devops.mission.control.service.health.cluster

import java.time.Duration

interface ClusterHealthReceiver {
    fun onSuccess(duration: Duration)

    fun onError(duration: Duration)

    fun <T> decorate(f: () -> T): T {
        val start = System.nanoTime()
        try {
            val result = f()
            val durationInNanos = System.nanoTime() - start
            onSuccess(Duration.ofNanos(durationInNanos))
            return result
        } catch (e: Throwable) {
            val durationInNanos = System.nanoTime() - start
            onError(Duration.ofNanos(durationInNanos))
            throw e
        }
    }
}