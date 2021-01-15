package ru.qiwi.devops.mission.control.web.model

import ru.qiwi.devops.mission.control.model.AnnotationInfo
import ru.qiwi.devops.mission.control.model.LabelInfo
import ru.qiwi.devops.mission.control.model.MetadataInfo
import java.time.ZonedDateTime

// Similar to K8S V1ObjectMeta,
// but passes only usable parameters
data class MetadataDTO(
    val name: String,
    val namespace: String,
    val labels: List<LabelInfo>,
    val annotations: List<AnnotationInfo>,
    val creationDateTime: ZonedDateTime
)

fun MetadataInfo.toDTO() = MetadataDTO(
    name = name,
    namespace = namespace,
    labels = labels,
    annotations = annotations,
    creationDateTime = creationDateTime.toZoned()
)