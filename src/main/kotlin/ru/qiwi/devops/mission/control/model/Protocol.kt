package ru.qiwi.devops.mission.control.model

import ru.qiwi.devops.mission.control.utils.getLogger
import java.lang.IllegalStateException

enum class Protocol {
    TCP, UDP, UNKNOWN;

    companion object {
        private val logger = getLogger<Protocol>()

        fun parse(name: String): Protocol {
            return try {
                valueOf(name)
            } catch (e: IllegalStateException) {
                logger.warn("Protocol $name has been mapped to Protocol.UNKNOWN")
                UNKNOWN
            }
        }
    }
}