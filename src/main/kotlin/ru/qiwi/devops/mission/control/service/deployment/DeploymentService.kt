package ru.qiwi.devops.mission.control.service.deployment

import ru.qiwi.devops.mission.control.model.DeploymentInfo

interface DeploymentService {
    fun getDeployments(deploymentCondition: String?): List<DeploymentInfo>

    fun findDeployment(namespace: String, deploymentName: String): DeploymentInfo?

    fun getDeploymentsByNamespace(namespace: String): List<DeploymentInfo>
}