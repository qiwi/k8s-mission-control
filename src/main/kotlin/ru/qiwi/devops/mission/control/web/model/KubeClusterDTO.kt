package ru.qiwi.devops.mission.control.web.model

data class KubeClusterDTO(
    val name: String,
    val displayName: String,
    val host: String,
    val dataCenter: String
)
