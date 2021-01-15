package ru.qiwi.devops.mission.control.model

import java.time.Instant

data class LogEntity(
    val timestamp: Instant,
    val pod: String,
    val container: String,
    val data: Map<String, String>
)