package ru.qiwi.devops.mission.control.service.health

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.service.cluster.ClusterService
import ru.qiwi.devops.mission.control.service.health.cluster.ClusterHealthMonitorSource
import ru.qiwi.devops.mission.control.service.health.cluster.ClusterHealthStatus

@Component
class KubernetesHealthIndicator(
    private val clusterService: ClusterService,
    private val monitorSource: ClusterHealthMonitorSource
) : HealthIndicator {
    override fun health(): Health {
        val statuses = clusterService.getClusters()
            .map { it.name to monitorSource.getMonitor(it.name).getCurrentStatus() }
            .filter { it.second.value != ClusterHealthStatus.OK }

        return Health.up()
            .apply {
                statuses.forEach {
                    this.withDetail(it.first, "Cluster is unhealthy")
                }
            }
            .build()
    }
}