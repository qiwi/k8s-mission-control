package ru.qiwi.devops.mission.control.web.model

import ru.qiwi.devops.mission.control.model.DeploymentInfo
import ru.qiwi.devops.mission.control.model.ImageInfo
import java.util.Locale

// metadata - contains common fields, it's backward compatible with K8S V1Deployment
// info - contains our calculated fields, moved here to prevent backward compatibility issues
data class DeploymentDTO(
    val metadata: MetadataDTO,
    val status: ResourceStatusInfoDTO,
    val info: DeploymentInfoDTO
)

data class DeploymentInfoDTO(
    val images: List<ImageInfo>,
    val replicas: Int
)

fun DeploymentInfo.toDTO(locale: Locale) = DeploymentDTO(
    metadata = metadata.toDTO(),
    status = status.toDTO(locale),
    info = DeploymentInfoDTO(
        images = images,
        replicas = replicas
    )
)