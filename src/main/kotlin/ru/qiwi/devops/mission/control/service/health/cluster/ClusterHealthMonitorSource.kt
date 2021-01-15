package ru.qiwi.devops.mission.control.service.health.cluster

interface ClusterHealthMonitorSource {
    fun getMonitor(clusterName: String): ClusterHealthMonitor
}