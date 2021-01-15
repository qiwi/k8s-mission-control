package ru.qiwi.devops.mission.control.service.pod

import io.kubernetes.client.openapi.models.V1ContainerStatus
import io.kubernetes.client.openapi.models.V1Pod
import ru.qiwi.devops.mission.control.model.ResourceStatusInfo

interface PodStatusMapper {
    fun mapStatus(pod: V1Pod): ResourceStatusInfo

    fun mapContainerStatus(containerStatus: V1ContainerStatus): ResourceStatusInfo
}