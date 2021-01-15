package ru.qiwi.devops.mission.control.service.log

import reactor.core.publisher.Flux
import ru.qiwi.devops.mission.control.model.LogEntity
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient
import ru.qiwi.devops.mission.control.service.log.parser.LogEntityParserImpl
import ru.qiwi.devops.mission.control.service.log.parser.LogParser
import ru.qiwi.devops.mission.control.utils.getLogger
import java.time.Instant
import kotlin.streams.toList

class LogServiceImpl(
    val client: KubernetesClient
) : LogService {
    private val TIMED_REQUEST_PERIOD = listOf(300, 900, 1800, 2700, 3600, 5400) // in second = (5, 15, 30, 45, 60, 90) in minutes

    private val logger = getLogger<LogServiceImpl>()
    private val parser: LogParser<LogEntity> = LogEntityParserImpl()

    override fun getDeploymentsLog(namespace: String, cluster: String, pod: String, container: String, date: Instant, before: Boolean, limit: Int): List<LogEntity> {
        return if (before) {
            getLogBeforeDate(namespace, pod, container, date, limit)
        } else {
            getLogSinceDate(namespace, pod, container, date, limit)
        }
    }

    override fun getStreamedDeploymentsLog(namespace: String, pod: String, container: String, date: Instant): Flux<LogEntity> {
        return getDeploymentsLogStream(namespace, pod, container, calculateSinceDatePeriod(date), true)
    }

    private fun getLogBeforeDate(namespace: String, pod: String, container: String, date: Instant, limit: Int): List<LogEntity> {
        var logList = emptyList<LogEntity>()
        val periodList = TIMED_REQUEST_PERIOD
        val since = calculateSinceDatePeriod(date)

        for (period in periodList) {
            logList = fromStream(namespace, pod, container, since + period)
                .filter { it.timestamp <= date }
            if (logList.count() >= limit)
                return logList.takeLast(limit)
        }
        return logList.takeLast(limit)
    }

    private fun getLogSinceDate(namespace: String, pod: String, container: String, date: Instant, limit: Int): List<LogEntity> {
        return fromStream(namespace, pod, container, calculateSinceDatePeriod(date))
            .sortedBy { it.timestamp }
            .take(limit)
    }

    private fun fromStream(namespace: String, pod: String, container: String, since: Int): List<LogEntity> {
        return this.getDeploymentsLogStream(namespace, pod, container, since, false)
            .toStream().toList()
    }

    private fun getDeploymentsLogStream(namespace: String, pod: String, container: String, since: Int, follow: Boolean): Flux<LogEntity> {
        return Flux.fromStream {
            client.streamNamespacedPodLog(namespace, pod, container, since, follow)
                .bufferedReader(Charsets.UTF_8)
                .lines()
                .map { parser.parseToEntity(pod, container, it) }
        }
    }

    /* Methods for getting log as whole string */
    private fun getLog(namespace: String, deployment: String, pod: String, container: String, since: Int, follow: Boolean): List<LogEntity> {
        return try {
            client.getNamespacedPodLog(namespace, pod, container, since, follow).orEmpty()
                .split("\n")
                .stream().map { log ->
                parser.parseToEntity(pod, container, log)
            }.filter { it.data.isNotEmpty() }.toList()
        } catch (e: Exception) {
            logger.error("logs for $namespace | $deployment | $pod | $container are unavailable.", e)
            emptyList()
        }
    }

    private fun calculateSinceDatePeriod(date: Instant): Int {
        val period = (Instant.now().epochSecond - date.epochSecond).toInt()
        return if (period == 0) 1 else period
    }
}