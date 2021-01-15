package ru.qiwi.devops.mission.control.model

import com.fasterxml.jackson.annotation.JsonUnwrapped

data class PodInfo(
    override val metadata: MetadataInfo,
    override val status: ResourceStatusInfo,
    val restartsCount: Int,
    val node: String?,
    val hostIp: String?,
    val podIp: String?,
    val ports: List<PortInfo>,
    val ownerReferences: List<PodOwnerReference>,
    val owners: List<PodOwnerInfo>?,
    val containers: List<PodContainerInfo>
) : AbstractResource

class PodOwnerReference(
    val kind: String,
    val name: String
)

sealed class PodOwnerInfo(
    val kind: String,
    val name: String
) {
    class UnknownPodOwnerInfo(kind: String, name: String) : PodOwnerInfo(kind, name)

    class ReplicaSetPodOwnerInfo(
        @get:JsonUnwrapped val replicaSet: ReplicaSetInfo
    ) : PodOwnerInfo("ReplicaSet", replicaSet.metadata.name)
}

data class PodContainerInfo(
    val name: String,
    val restartsCount: Int,
    val image: ImageInfo?,
    val ports: List<PortInfo>,
    val status: ResourceStatusInfo
)