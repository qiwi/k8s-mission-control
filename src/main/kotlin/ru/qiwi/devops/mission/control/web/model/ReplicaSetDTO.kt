package ru.qiwi.devops.mission.control.web.model

import ru.qiwi.devops.mission.control.model.ImageInfo
import ru.qiwi.devops.mission.control.model.ReplicaSetInfo
import java.util.Locale

data class ReplicaSetDTO(
    val metadata: MetadataDTO,
    val status: ResourceStatusInfoDTO,
    val info: ReplicaSetInfoDTO
)

data class ReplicaSetInfoDTO(
    val images: List<ImageInfo>,
    val desiredPods: Int,
    val availablePods: Int,
    val pods: List<PodDTO>?
)

fun ReplicaSetInfo.toDTO(locale: Locale) = ReplicaSetDTO(
    metadata = metadata.toDTO(),
    status = status.toDTO(locale),
    info = ReplicaSetInfoDTO(
        images = images,
        desiredPods = desiredPods,
        availablePods = availablePods,
        pods = null
    )
)