package ru.qiwi.devops.mission.control.model

data class ReplicaSetInfo(
    override val metadata: MetadataInfo,
    override val status: ResourceStatusInfo,
    val images: List<ImageInfo>,
    val podSelectionLabels: List<LabelInfo>?,
    val desiredPods: Int,
    val availablePods: Int
) : AbstractResource
