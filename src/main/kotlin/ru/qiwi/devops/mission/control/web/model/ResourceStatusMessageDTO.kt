package ru.qiwi.devops.mission.control.web.model

import ru.qiwi.devops.mission.control.model.ResourceStatusLevel
import ru.qiwi.devops.mission.control.model.ResourceStatusMessage
import java.time.ZonedDateTime
import java.util.Locale

data class ResourceStatusMessageDTO(
    val level: ResourceStatusLevel,
    val clarity: Int,
    val dateTime: ZonedDateTime,
    val key: String,
    val params: Map<String, String>,
    val userMessage: String
)

fun ResourceStatusMessage.toDTO(locale: Locale): ResourceStatusMessageDTO {
    return ResourceStatusMessageDTO(
        level = level,
        clarity = clarity.value,
        dateTime = dateTime.toZoned(),
        key = message.key,
        params = message.params,
        userMessage = message.getUserMessage(locale)
    )
}