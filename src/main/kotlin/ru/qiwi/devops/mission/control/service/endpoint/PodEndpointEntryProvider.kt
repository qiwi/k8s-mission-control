package ru.qiwi.devops.mission.control.service.endpoint

import ru.qiwi.devops.mission.control.model.PodInfo
import ru.qiwi.devops.mission.control.model.EndpointAddress
import ru.qiwi.devops.mission.control.model.EndpointOwner
import ru.qiwi.devops.mission.control.model.EndpointOwnerType
import ru.qiwi.devops.mission.control.model.RawEndpoint

class PodEndpointEntryProvider(
    private val pods: List<PodInfo>
) : EndpointEntryProvider {
    override fun getEntries(namespace: String, deploymentName: String): Iterable<RawEndpoint> {
        return pods.flatMap { pod -> pod.getEndpoints() }
    }

    private fun PodInfo.getEndpoints(): List<RawEndpoint> {
        val owner = EndpointOwner(EndpointOwnerType.POD, this.metadata.name)

        return this.ports.map { port ->
            RawEndpoint(
                owner = owner,
                name = "Pod IP",
                address = EndpointAddress.HostEndpointAddress(this.podIp
                    ?: "unknown", port.port),
                targetPort = port.port.toString()
            )
        }
    }
}