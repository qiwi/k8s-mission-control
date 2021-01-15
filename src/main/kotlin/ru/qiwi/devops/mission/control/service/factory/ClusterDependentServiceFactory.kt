package ru.qiwi.devops.mission.control.service.factory

import ru.qiwi.devops.mission.control.service.deployment.DeploymentService
import ru.qiwi.devops.mission.control.service.endpoint.EndpointService
import ru.qiwi.devops.mission.control.service.log.LogService
import ru.qiwi.devops.mission.control.service.pod.PodService
import ru.qiwi.devops.mission.control.service.replicaset.ReplicaSetService
import ru.qiwi.devops.mission.control.service.service.ServiceService

interface ClusterDependentServiceFactory {
    fun createDeploymentService(clusterName: String): DeploymentService

    fun createPodService(clusterName: String): PodService

    fun createReplicaSetService(clusterName: String): ReplicaSetService

    fun createServiceService(clusterName: String): ServiceService

    fun createEndpointService(clusterName: String): EndpointService

    fun createLogService(clusterName: String): LogService
}