package ru.qiwi.devops.mission.control.service.endpoint

import ru.qiwi.devops.mission.control.model.RawEndpoint

interface EndpointEntryProvider {
    fun getEntries(namespace: String, deploymentName: String): Iterable<RawEndpoint>
}
