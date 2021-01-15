package ru.qiwi.devops.mission.control.model

import ru.qiwi.devops.mission.control.messages.Message
import java.time.Instant

data class ResourceStatusMessage(
    val level: ResourceStatusLevel,
    val clarity: ResourceStatusMessageClarity,
    val dateTime: Instant,
    val message: Message
)