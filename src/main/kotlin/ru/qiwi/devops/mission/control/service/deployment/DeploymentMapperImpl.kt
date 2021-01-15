package ru.qiwi.devops.mission.control.service.deployment

import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.openapi.models.V1DeploymentCondition
import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.messages.DeploymentMessages.deploymentDoesNotHaveCondition
import ru.qiwi.devops.mission.control.messages.DeploymentMessages.deploymentIsInProgressingState
import ru.qiwi.devops.mission.control.messages.DeploymentMessages.deploymentIsNotInAvailableState
import ru.qiwi.devops.mission.control.messages.MessagesService
import ru.qiwi.devops.mission.control.model.DeploymentInfo
import ru.qiwi.devops.mission.control.model.ImageInfo
import ru.qiwi.devops.mission.control.model.ResourceStatusInfo
import ru.qiwi.devops.mission.control.service.parseImage
import ru.qiwi.devops.mission.control.service.toMetadataInfo

@Component
class DeploymentMapperImpl(
    private val messagesService: MessagesService
) : DeploymentMapper {
    override fun mapDeployment(deployment: V1Deployment, clusterName: String): DeploymentInfo {
        return DeploymentInfo(
            metadata = deployment.metadata.toMetadataInfo(clusterName),
            status = determineStatus(deployment),
            images = deployment.getImages(),
            replicas = deployment.status?.replicas ?: 0
        )
    }

    private fun determineStatus(deployment: V1Deployment): ResourceStatusInfo {
        return ResourceStatusInfo.build {
            val progressing = deployment.getCondition("Progressing")
            if (progressing == null) {
                error(messagesService.deploymentDoesNotHaveCondition("Progressing"))
            } else if (progressing.status != "True") {
                error(messagesService.deploymentIsInProgressingState(progressing.message ?: ""))
            }

            val available = deployment.getCondition("Available")
            if (available == null) {
                error(messagesService.deploymentDoesNotHaveCondition("Available"))
            } else if (available.status != "True") {
                error(messagesService.deploymentIsNotInAvailableState(available.message ?: ""))
            }
        }
    }

    private fun V1Deployment.getImages(): List<ImageInfo> {
        return this.spec?.template?.spec?.containers
            ?.map { parseImage(it.image.orEmpty()) }
            ?: emptyList()
    }

    private fun V1Deployment.getCondition(type: String): V1DeploymentCondition? {
        return this.status?.conditions?.firstOrNull {
            it.type == type
        }
    }
}