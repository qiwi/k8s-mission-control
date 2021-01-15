package ru.qiwi.devops.mission.control.model

enum class ResourceStatusMessageClarity(val value: Int) {
    NO(0), // for informational messages
    LOW(10), // messages are built from k8s statuses, without some AI,
    HIGH(70), // it's probably maybe the reason, but you need to clarify real reason
    HIGHEST(100) // lets you know exactly what happened
}