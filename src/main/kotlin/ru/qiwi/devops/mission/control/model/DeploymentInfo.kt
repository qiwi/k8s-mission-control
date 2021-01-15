package ru.qiwi.devops.mission.control.model

data class DeploymentInfo(
    override val metadata: MetadataInfo,
    override val status: ResourceStatusInfo,
    val images: List<ImageInfo>,
    val replicas: Int
) : AbstractResource
