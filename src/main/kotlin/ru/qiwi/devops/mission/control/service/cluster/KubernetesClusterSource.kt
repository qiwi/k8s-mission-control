package ru.qiwi.devops.mission.control.service.cluster

import ru.qiwi.devops.mission.control.model.KubeCluster

interface KubernetesClusterSource {
    fun getClusters(): List<KubeCluster>
}