package ru.qiwi.devops.mission.control.model.ingress

data class IngressHttpPath(
    val path: String,
    val backend: IngressHttpBackend
)