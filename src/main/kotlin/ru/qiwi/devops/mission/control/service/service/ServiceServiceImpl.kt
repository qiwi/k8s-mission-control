package ru.qiwi.devops.mission.control.service.service

import io.kubernetes.client.openapi.models.V1Service
import ru.qiwi.devops.mission.control.model.ServicePortInfo
import ru.qiwi.devops.mission.control.model.ResourceStatusInfo
import ru.qiwi.devops.mission.control.model.ServiceInfo
import ru.qiwi.devops.mission.control.model.Protocol
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient
import ru.qiwi.devops.mission.control.service.toMetadataInfo
import ru.qiwi.devops.mission.control.utils.getLogger

class ServiceServiceImpl(
    val client: KubernetesClient
) : ServiceService {
    private val logger = getLogger<ServiceServiceImpl>()

    override fun getServicesByDeployment(namespace: String, deploymentName: String): List<ServiceInfo> {
        logger.info("Looking for services associated with deployment $namespace/$deploymentName in cluster ${client.clusterName}...")
        return client.findService(namespace, deploymentName)
            ?.toServiceInfo()
            ?.let { listOf(it) } ?: emptyList()
    }

    private fun V1Service.toServiceInfo() = ServiceInfo(
        metadata = this.metadata.toMetadataInfo(client.clusterName),
        status = ResourceStatusInfo.OK,
        clusterIp = this.spec?.clusterIP,
        ports = this.spec?.ports?.map { ServicePortInfo(
            name = it.name ?: "",
            nodePort = it.nodePort,
            port = it.port,
            targetPort = it.targetPort?.toString(),
            protocol = it.protocol?.let { protocol -> Protocol.parse(protocol) } ?: Protocol.UNKNOWN
        ) } ?: emptyList(),
        type = this.spec?.type
    )
}