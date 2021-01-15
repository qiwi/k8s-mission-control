package ru.qiwi.devops.mission.control.web.model

data class CreateSessionDTO(
    val userName: String,
    val password: String,
    val cookie: Boolean? = null
)