package ru.qiwi.devops.mission.control.platform.kubernetes

import ru.qiwi.devops.mission.control.platform.configuration.TestConfig
import java.time.Duration

data class KubernetesConfig(
    val defaultCluster: String?,
    val waitForEventMs: Long,
    val generating: GeneratingConfig,
    val clusters: List<ClusterConfig>
) {
    val waitForEvent = Duration.ofMillis(waitForEventMs)
}

data class GeneratingConfig(
    val namespace: String,
    val namePrefix: String
)

data class ClusterConfig(
    val name: String,
    val host: String,
    val token: String
)

val TestConfig.kubernetes: KubernetesConfig
    get() = load("kubernetes")