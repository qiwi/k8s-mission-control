package ru.qiwi.devops.mission.control.service.endpoint

import ru.qiwi.devops.mission.control.model.Endpoint

interface EndpointService {
    fun getEndpoints(namespace: String, deploymentName: String): List<Endpoint>
}
