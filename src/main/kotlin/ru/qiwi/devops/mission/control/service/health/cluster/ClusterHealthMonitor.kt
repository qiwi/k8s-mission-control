package ru.qiwi.devops.mission.control.service.health.cluster

interface ClusterHealthMonitor {
    fun getCurrentStatus(): ClusterHealth

    fun getReceiver(): ClusterHealthReceiver
}