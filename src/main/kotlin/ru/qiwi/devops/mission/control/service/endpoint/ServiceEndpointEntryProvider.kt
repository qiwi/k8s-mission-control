package ru.qiwi.devops.mission.control.service.endpoint

import ru.qiwi.devops.mission.control.model.ServiceInfo
import ru.qiwi.devops.mission.control.model.EndpointAddress
import ru.qiwi.devops.mission.control.model.EndpointOwner
import ru.qiwi.devops.mission.control.model.EndpointOwnerType
import ru.qiwi.devops.mission.control.model.RawEndpoint

class ServiceEndpointEntryProvider(
    private val services: List<ServiceInfo>
) : EndpointEntryProvider {
    override fun getEntries(namespace: String, deploymentName: String): Iterable<RawEndpoint> {
        return services.flatMap { it.getEndpoints() }
    }

    private fun ServiceInfo.getEndpoints(): List<RawEndpoint> {
        val owner = EndpointOwner(EndpointOwnerType.SERVICE, this.metadata.name)

        return this.ports.flatMap { port ->
            val targetPort = port.targetPort ?: "0"

            listOfNotNull(
                this.clusterIp?.let { clusterIp ->
                    if (clusterIp != "None") {
                        RawEndpoint(
                            owner = owner,
                            name = "Service port",
                            address = EndpointAddress.HostEndpointAddress(clusterIp, port.port),
                            targetPort = targetPort
                        )
                    } else null
                },
                port.nodePort?.let { nodePort ->
                    RawEndpoint(
                        owner = owner,
                        name = "Node port",
                        address = EndpointAddress.NodePortEndpointAddress(nodePort),
                        targetPort = targetPort
                    )
                }
            )
        }
    }
}