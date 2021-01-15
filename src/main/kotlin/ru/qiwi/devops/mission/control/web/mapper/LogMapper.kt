package ru.qiwi.devops.mission.control.web.mapper

import ru.qiwi.devops.mission.control.model.LogEntity
import ru.qiwi.devops.mission.control.web.model.logs.LogEntityDTO

fun List<LogEntity>.toLogEntityDTO(): LogEntityDTO {
    return LogEntityDTO(this)
}