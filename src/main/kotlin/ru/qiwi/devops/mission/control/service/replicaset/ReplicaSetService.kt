package ru.qiwi.devops.mission.control.service.replicaset

import ru.qiwi.devops.mission.control.model.ReplicaSetInfo

interface ReplicaSetService {
    fun getReplicaSetsByDeployment(namespace: String, deploymentName: String): List<ReplicaSetInfo>

    fun findReplicaSet(namespace: String, replicaSetName: String): ReplicaSetInfo?
}