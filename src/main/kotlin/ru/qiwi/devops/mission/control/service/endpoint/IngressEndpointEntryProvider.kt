package ru.qiwi.devops.mission.control.service.endpoint

import ru.qiwi.devops.mission.control.model.ServiceInfo
import ru.qiwi.devops.mission.control.model.EndpointAddress
import ru.qiwi.devops.mission.control.model.EndpointOwner
import ru.qiwi.devops.mission.control.model.EndpointOwnerType
import ru.qiwi.devops.mission.control.model.RawEndpoint
import ru.qiwi.devops.mission.control.model.ingress.IngressInfo
import ru.qiwi.devops.mission.control.service.ingress.IngressCache

class IngressEndpointEntryProvider(
    private val services: List<ServiceInfo>,
    private val ingressCache: IngressCache
) : EndpointEntryProvider {
    override fun getEntries(namespace: String, deploymentName: String): Iterable<RawEndpoint> {
        return services.flatMap { svc ->
            ingressCache
                .getByService(svc.metadata.clusterName, svc.metadata.namespace, svc.metadata.name)
                .flatMap { ing -> ing.getEndpoints() }
        }
    }

    private fun IngressInfo.getEndpoints(): List<RawEndpoint> {
        val owner = EndpointOwner(EndpointOwnerType.INGRESS, this.metadata.name)

        return this.rules.flatMap { rule ->
            rule.http.map { http ->
                RawEndpoint(
                    owner = owner,
                    name = "Ingress URL",
                    address = EndpointAddress.URLEndpointAddress("https://${rule.host}${http.path}"),
                    targetPort = http.backend.servicePort
                )
            }
        }
    }
}