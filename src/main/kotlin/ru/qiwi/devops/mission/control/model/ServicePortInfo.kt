package ru.qiwi.devops.mission.control.model

data class ServicePortInfo(
    override val name: String,
    val nodePort: Int?,
    override val port: Int,
    override val protocol: Protocol,
    val targetPort: String?
) : AbstractPort