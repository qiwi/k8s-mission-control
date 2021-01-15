package ru.qiwi.devops.mission.control.service

import io.kubernetes.client.openapi.models.V1ObjectMeta
import ru.qiwi.devops.mission.control.model.AnnotationInfo
import ru.qiwi.devops.mission.control.model.ImageInfo
import ru.qiwi.devops.mission.control.model.LabelInfo
import ru.qiwi.devops.mission.control.model.MetadataInfo
import ru.qiwi.devops.mission.control.utils.orZeroTime
import ru.qiwi.devops.mission.control.utils.toJavaInstant

fun V1ObjectMeta?.toMetadataInfo(clusterName: String) = MetadataInfo(
    name = this?.name ?: "",
    namespace = this?.namespace ?: "",
    labels = this.getLabels(),
    annotations = this.getAnnotations(),
    creationDateTime = this?.creationTimestamp?.toJavaInstant().orZeroTime(),
    clusterName = clusterName
)

fun V1ObjectMeta?.getLabels(): List<LabelInfo> {
    return this?.labels
        ?.map { LabelInfo(it.key, it.value) }
        ?: emptyList()
}

fun V1ObjectMeta?.getAnnotations(): List<AnnotationInfo> {
    return this?.annotations
        ?.map { AnnotationInfo(it.key, it.value) }
        ?: emptyList()
}

fun parseImage(imageName: String): ImageInfo {
    val matchedGroups = "^(.*?)/(.*):(.*)\$".toRegex().matchEntire(imageName)?.destructured
    return ImageInfo(
        imageName,
        matchedGroups?.component1() ?: "",
        matchedGroups?.component2() ?: "",
        matchedGroups?.component3() ?: ""
    )
}