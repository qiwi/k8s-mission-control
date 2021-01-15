package ru.qiwi.devops.mission.control.service.cluster

import ru.qiwi.devops.mission.control.model.KubeCluster
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient

interface ClusterService {
    fun getClusters(): List<KubeCluster>

    fun getClusterClient(clusterName: String): KubernetesClient?
}
