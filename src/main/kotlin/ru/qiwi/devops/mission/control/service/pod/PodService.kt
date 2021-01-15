package ru.qiwi.devops.mission.control.service.pod

import ru.qiwi.devops.mission.control.model.PodInfo

interface PodService {
    fun getPodsByDeployments(namespace: String, deploymentName: String): List<PodInfo>

    fun getPodsByReplicaSet(namespace: String, replicaSetName: String): List<PodInfo>

    fun findPod(namespace: String, podName: String): PodInfo?
}
