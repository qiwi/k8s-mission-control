package ru.qiwi.devops.mission.control.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import ru.qiwi.devops.mission.control.service.health.cluster.ClusterHealthMonitorConfig

@ConstructorBinding
@ConfigurationProperties("mission-control.health")
data class HealthProperties(
    @get:Bean
    val clusters: ClustersHealthMonitorProperties = ClustersHealthMonitorProperties()
)

data class ClustersHealthMonitorProperties(
    override val minNumberOfCalls: Int = 10,
    override val failureRateThreshold: Int = 70,
    override val slidingWindowSize: Int = 10
) : ClusterHealthMonitorConfig