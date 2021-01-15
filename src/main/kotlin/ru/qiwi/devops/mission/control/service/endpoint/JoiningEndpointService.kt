package ru.qiwi.devops.mission.control.service.endpoint

import ru.qiwi.devops.mission.control.model.PortInfo
import ru.qiwi.devops.mission.control.model.PortsMap
import ru.qiwi.devops.mission.control.model.Protocol
import ru.qiwi.devops.mission.control.model.ServicePortInfo
import ru.qiwi.devops.mission.control.model.Endpoint
import ru.qiwi.devops.mission.control.model.RawEndpoint
import ru.qiwi.devops.mission.control.service.ingress.IngressCache
import ru.qiwi.devops.mission.control.service.pod.PodService
import ru.qiwi.devops.mission.control.service.service.ServiceService

class JoiningEndpointService(
    private val podService: PodService,
    private val serviceService: ServiceService,
    private val ingressCache: IngressCache
) : EndpointService {
    override fun getEndpoints(namespace: String, deploymentName: String): List<Endpoint> {
        val pods = podService.getPodsByDeployments(namespace, deploymentName)
        val services = serviceService.getServicesByDeployment(namespace, deploymentName)

        val providers = listOf(
            PodEndpointEntryProvider(pods),
            ServiceEndpointEntryProvider(services),
            IngressEndpointEntryProvider(services, ingressCache)
        )

        val podPorts = PortsMap.create(pods.flatMap { it.ports })
        val servicePorts = PortsMap.create(services.flatMap { it.ports })

        return providers.flatMap { it.getEntries(namespace, deploymentName) }
            .map { mapToEndpoint(it, podPorts, servicePorts) }
    }

    private fun mapToEndpoint(endpoint: RawEndpoint, podPorts: PortsMap<PortInfo>, servicePorts: PortsMap<ServicePortInfo>): Endpoint {
        return endpoint.toEndpoint { targetPort ->
            val servicePort = servicePorts.findOne(targetPort)

            podPorts.findOne(targetPort)
                ?.let { if (it.name.isBlank() && servicePort != null) it.copy(name = servicePort.name) else it }
                ?: PortInfo(targetPort.toIntOrNull() ?: 0, targetPort, Protocol.UNKNOWN)
        }
    }
}