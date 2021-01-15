package ru.qiwi.devops.mission.control.model.ingress

data class IngressRuleInfo(
    val host: String,
    val http: List<IngressHttpPath>
)