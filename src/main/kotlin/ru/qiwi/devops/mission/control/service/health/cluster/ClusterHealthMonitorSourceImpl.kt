package ru.qiwi.devops.mission.control.service.health.cluster

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class ClusterHealthMonitorSourceImpl(
    private val config: ClusterHealthMonitorConfig
) : ClusterHealthMonitorSource {
    private val monitors = ConcurrentHashMap<String, ClusterHealthMonitor>()

    override fun getMonitor(clusterName: String): ClusterHealthMonitor {
        return monitors.computeIfAbsent(clusterName) { cn ->
            ClusterHealthMonitorImpl(cn, config)
        }
    }
}