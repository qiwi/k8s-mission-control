package ru.qiwi.devops.mission.control.model

data class ServiceInfo(
    override val metadata: MetadataInfo,
    override val status: ResourceStatusInfo,
    val clusterIp: String?,
    val ports: List<ServicePortInfo>,
    val type: String?
) : AbstractResource
