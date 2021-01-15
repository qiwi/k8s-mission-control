package ru.qiwi.devops.mission.control.web.model

import ru.qiwi.devops.mission.control.model.PodContainerInfo
import ru.qiwi.devops.mission.control.model.PodInfo
import ru.qiwi.devops.mission.control.model.PodOwnerInfo
import java.util.Locale

data class PodDTO(
    val metadata: MetadataDTO,
    val status: ResourceStatusInfoDTO,
    val info: PodInfoDTO
)

data class PodInfoDTO(
    val restartsCount: Int,
    val node: String?,
    val hostIp: String?,
    val podIp: String?,
    val owners: List<PodOwnerInfo>?,
    val containers: List<PodContainerInfo>?
)

fun PodInfo.toDTO(locale: Locale) = PodDTO(
    metadata = metadata.toDTO(),
    status = status.toDTO(locale),
    info = PodInfoDTO(
        restartsCount = restartsCount,
        node = node,
        hostIp = hostIp,
        podIp = podIp,
        owners = null,
        containers = null
    )
)

fun PodInfo.toDetailedDTO(locale: Locale) = PodDTO(
    metadata = metadata.toDTO(),
    status = status.toDTO(locale),
    info = PodInfoDTO(
        restartsCount = restartsCount,
        node = node,
        hostIp = hostIp,
        podIp = podIp,
        owners = owners,
        containers = containers
    )
)