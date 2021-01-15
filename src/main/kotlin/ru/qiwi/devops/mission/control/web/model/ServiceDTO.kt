package ru.qiwi.devops.mission.control.web.model

import ru.qiwi.devops.mission.control.model.ServicePortInfo
import ru.qiwi.devops.mission.control.model.ServiceInfo
import java.util.Locale

data class ServiceDTO(
    val metadata: MetadataDTO,
    val status: ResourceStatusInfoDTO,
    val info: ServiceInfoDTO
)

data class ServiceInfoDTO(
    val clusterIp: String?,
    val ports: List<ServicePortInfo>,
    val type: String?
)

fun ServiceInfo.toDTO(locale: Locale) = ServiceDTO(
    metadata = metadata.toDTO(),
    status = status.toDTO(locale),
    info = ServiceInfoDTO(
        clusterIp = clusterIp,
        ports = ports,
        type = type
    )
)