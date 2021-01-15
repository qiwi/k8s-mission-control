package ru.qiwi.devops.mission.control.web.model

import ru.qiwi.devops.mission.control.model.ResourceStatusInfo
import ru.qiwi.devops.mission.control.model.ResourceStatusLevel
import java.util.Locale

data class ResourceStatusInfoDTO(
    val value: ResourceStatusLevel,
    val messages: List<ResourceStatusMessageDTO>
)

fun ResourceStatusInfo.toDTO(locale: Locale): ResourceStatusInfoDTO {
    return ResourceStatusInfoDTO(
        value = value,
        messages = messages.map { it.toDTO(locale) }
    )
}