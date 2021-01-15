package ru.qiwi.devops.mission.control.service.log

import reactor.core.publisher.Flux
import ru.qiwi.devops.mission.control.model.LogEntity
import java.time.Instant

interface LogService {
    fun getDeploymentsLog(namespace: String, cluster: String, pod: String, container: String, date: Instant, before: Boolean, limit: Int): List<LogEntity>

    fun getStreamedDeploymentsLog(namespace: String, pod: String, container: String, date: Instant): Flux<LogEntity>
}