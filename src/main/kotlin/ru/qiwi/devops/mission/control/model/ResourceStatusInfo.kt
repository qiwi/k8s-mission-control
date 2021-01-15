package ru.qiwi.devops.mission.control.model

import ru.qiwi.devops.mission.control.messages.Message
import java.time.Instant

data class ResourceStatusInfo(
    val value: ResourceStatusLevel,
    val messages: List<ResourceStatusMessage> = emptyList()
) {
    companion object {
        val OK = ResourceStatusInfo(ResourceStatusLevel.OK)

        val UNKNOWN = ResourceStatusInfo(ResourceStatusLevel.INACTIVE)

        fun build(fn: Builder.() -> Unit): ResourceStatusInfo {
            return Builder(ResourceStatusLevel.OK).apply(fn).build()
        }
    }

    class Builder(
        val defaultLevel: ResourceStatusLevel
    ) {
        private val messages = mutableListOf<ResourceStatusMessage>()

        fun warn(message: Message, clarity: ResourceStatusMessageClarity = ResourceStatusMessageClarity.NO, dateTime: Instant = Instant.now()): Builder {
            return add(ResourceStatusLevel.WARN, clarity, message, dateTime)
        }

        fun error(message: Message, clarity: ResourceStatusMessageClarity = ResourceStatusMessageClarity.LOW, dateTime: Instant = Instant.now()): Builder {
            return add(ResourceStatusLevel.ERROR, clarity, message, dateTime)
        }

        fun info(message: Message, clarity: ResourceStatusMessageClarity = ResourceStatusMessageClarity.NO, dateTime: Instant = Instant.now()): Builder {
            return add(ResourceStatusLevel.OK, clarity, message, dateTime)
        }

        private fun add(level: ResourceStatusLevel, clarity: ResourceStatusMessageClarity, message: Message, dateTime: Instant = Instant.now()): Builder {
            messages.add(ResourceStatusMessage(level, clarity, dateTime, message))
            return this
        }

        fun build(): ResourceStatusInfo {
            val maxLevel = messages.maxBy { it.level }?.level ?: defaultLevel
            return ResourceStatusInfo(maxLevel, messages)
        }
    }
}