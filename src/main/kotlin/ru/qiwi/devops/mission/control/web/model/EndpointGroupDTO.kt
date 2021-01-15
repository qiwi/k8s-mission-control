package ru.qiwi.devops.mission.control.web.model

import ru.qiwi.devops.mission.control.model.PortInfo
import ru.qiwi.devops.mission.control.model.Endpoint

data class EndpointGroupDTO(
    val target: PortInfo,
    val addresses: List<Endpoint>
)