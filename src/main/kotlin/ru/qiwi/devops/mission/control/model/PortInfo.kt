package ru.qiwi.devops.mission.control.model

data class PortInfo(
    override val port: Int,
    override val name: String,
    override val protocol: Protocol
) : AbstractPort