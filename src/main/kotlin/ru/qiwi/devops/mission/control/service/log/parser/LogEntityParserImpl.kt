package ru.qiwi.devops.mission.control.service.log.parser

import ru.qiwi.devops.mission.control.model.LogEntity
import java.time.Instant

class LogEntityParserImpl : LogParser<LogEntity> {
    private val logDataParser = LogDataParser()

    override fun parseToEntity(pod: String, container: String, log: String): LogEntity {
        return if (log.isNotEmpty()) {
            try {
                val splitLog = log.split(" ", ignoreCase = true, limit = 2)
                val data = logDataParser.parse(splitLog[1])
                createEntity(Instant.parse(splitLog[0]), pod, container, data)
            } catch (e: Exception) {
                createEntity(Instant.now(), pod, container, emptyMap())
            }
        } else createEntity(Instant.now(), pod, container, emptyMap())
    }

    private fun createEntity(date: Instant, pod: String, container: String, data: Map<String, String>): LogEntity {
        return LogEntity(
            timestamp = date,
            pod = pod,
            container = container,
            data = data)
    }
}