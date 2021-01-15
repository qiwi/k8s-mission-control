package ru.qiwi.devops.mission.control.web.model.logs

import ru.qiwi.devops.mission.control.model.LogEntity

data class LogEntityDTO(
    val logList: List<LogEntity>
)