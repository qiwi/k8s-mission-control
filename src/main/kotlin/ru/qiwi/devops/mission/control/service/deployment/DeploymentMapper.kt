package ru.qiwi.devops.mission.control.service.deployment

import io.kubernetes.client.openapi.models.V1Deployment
import ru.qiwi.devops.mission.control.model.DeploymentInfo

interface DeploymentMapper {
    fun mapDeployment(deployment: V1Deployment, clusterName: String): DeploymentInfo
}