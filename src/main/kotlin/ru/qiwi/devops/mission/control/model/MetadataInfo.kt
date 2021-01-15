package ru.qiwi.devops.mission.control.model

import java.time.Instant

// Similar to K8S V1ObjectMeta,
// but passes only usable parameters
data class MetadataInfo(
    val name: String,
    val namespace: String,
    val labels: List<LabelInfo>,
    val annotations: List<AnnotationInfo>,
    val creationDateTime: Instant,
    val clusterName: String
)