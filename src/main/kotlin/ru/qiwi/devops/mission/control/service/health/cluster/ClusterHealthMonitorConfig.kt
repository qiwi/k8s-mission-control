package ru.qiwi.devops.mission.control.service.health.cluster

interface ClusterHealthMonitorConfig {
    val minNumberOfCalls: Int
    val failureRateThreshold: Int
    val slidingWindowSize: Int
}