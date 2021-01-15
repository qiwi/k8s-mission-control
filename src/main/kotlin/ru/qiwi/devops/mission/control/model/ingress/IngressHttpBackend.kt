package ru.qiwi.devops.mission.control.model.ingress

data class IngressHttpBackend(
    val serviceName: String,
    val servicePort: String
)