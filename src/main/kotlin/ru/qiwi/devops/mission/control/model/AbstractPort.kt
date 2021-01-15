package ru.qiwi.devops.mission.control.model

interface AbstractPort {
    val port: Int
    val name: String
    val protocol: Protocol
}