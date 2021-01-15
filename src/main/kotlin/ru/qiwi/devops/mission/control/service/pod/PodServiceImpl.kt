package ru.qiwi.devops.mission.control.service.pod

import io.kubernetes.client.openapi.models.V1Pod
import ru.qiwi.devops.mission.control.model.PodContainerInfo
import ru.qiwi.devops.mission.control.web.errors.ApiErrors
import ru.qiwi.devops.mission.control.model.PodInfo
import ru.qiwi.devops.mission.control.model.PodOwnerInfo
import ru.qiwi.devops.mission.control.model.PodOwnerReference
import ru.qiwi.devops.mission.control.model.PortInfo
import ru.qiwi.devops.mission.control.model.Protocol
import ru.qiwi.devops.mission.control.model.ReplicaSetInfo
import ru.qiwi.devops.mission.control.model.ResourceStatusInfo
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient
import ru.qiwi.devops.mission.control.service.parseImage
import ru.qiwi.devops.mission.control.service.replicaset.ReplicaSetService
import ru.qiwi.devops.mission.control.service.toMetadataInfo
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.web.errors.ApiException

class PodServiceImpl(
    private val client: KubernetesClient,
    private val replicaSetService: ReplicaSetService,
    private val podStatusMapper: PodStatusMapper
) : PodService {

    private val logger = getLogger<PodServiceImpl>()

    override fun getPodsByDeployments(namespace: String, deploymentName: String): List<PodInfo> {
        logger.info("Looking for pods created by $namespace/$deploymentName in ${client.clusterName}...")

        return replicaSetService.getReplicaSetsByDeployment(namespace, deploymentName)
            .flatMap { getPodsByReplicaSet(it) }
    }

    override fun getPodsByReplicaSet(namespace: String, replicaSetName: String): List<PodInfo> {
        logger.info("Looking for pods created by $namespace/$replicaSetName in ${client.clusterName}...")

        return replicaSetService.findReplicaSet(namespace, replicaSetName)
            ?.let { getPodsByReplicaSet(it) }
            ?: emptyList()
    }

    override fun findPod(namespace: String, podName: String): PodInfo? {
        logger.info("Looking for pod $namespace/$podName in ${client.clusterName}...")

        return client.findPod(namespace, podName)
            ?.toPodInfo(client.clusterName)
            ?.let { enrichPodWithOwners(it) } ?: run {
                logger.info("Pod $namespace/$podName not found")
                null
            }
    }

    private fun enrichPodWithOwners(pod: PodInfo): PodInfo {
        return pod.copy(
            owners = pod.ownerReferences.map { ref ->
                when (ref.kind) {
                    "ReplicaSet" -> replicaSetService.findReplicaSet(pod.metadata.namespace, ref.name)
                        ?.let { PodOwnerInfo.ReplicaSetPodOwnerInfo(it) } ?: run {
                        logger.info("Replica set ${pod.metadata.namespace}/${ref.name} owns pod ${pod.metadata.name}, but doesn't exist")
                        PodOwnerInfo.UnknownPodOwnerInfo(ref.kind, ref.name)
                    }
                    else -> PodOwnerInfo.UnknownPodOwnerInfo(ref.kind, ref.name)
                }
            }
        )
    }

    private fun getPodsByReplicaSet(replicaSet: ReplicaSetInfo): List<PodInfo> {
        val selector = createLabelSelector(replicaSet)
        logger.debug("Looking for pods in cluster ${client.clusterName} using selector '$selector'...")
        return client.listPodsBySelector(replicaSet.metadata.namespace, selector)
            .filter { it.isOwnedByReplicaSet(replicaSet.metadata.name) }
            .map { it.toPodInfo(replicaSet.metadata.clusterName) }
    }

    private fun createLabelSelector(replicaSet: ReplicaSetInfo): String {
        return replicaSet.podSelectionLabels
            ?.joinToString(",") { "${it.key}=${it.value}" }
            ?: throw canNotDiscoverPods()
    }

    private fun V1Pod.isOwnedByReplicaSet(replicaSetName: String): Boolean {
        return this.metadata?.ownerReferences?.any {
            it.kind == "ReplicaSet" && it.name == replicaSetName
        } ?: false
    }

    private fun V1Pod.toPodInfo(clusterName: String): PodInfo {
        return PodInfo(
            metadata = this.metadata.toMetadataInfo(clusterName),
            status = podStatusMapper.mapStatus(this),
            restartsCount = getContainerStatuses(this)?.map { it.restartCount }?.max() ?: 0,
            node = this.spec?.nodeName,
            hostIp = this.status?.hostIP,
            podIp = this.status?.podIP,
            ports = this.spec?.containers?.flatMap { container ->
                container.ports?.map { port ->
                    PortInfo(
                        port = port.containerPort ?: 0,
                        name = port.name ?: "",
                        protocol = port.protocol?.let { Protocol.parse(it) } ?: Protocol.UNKNOWN
                    )
                } ?: emptyList()
            } ?: emptyList(),
            ownerReferences = getOwnerReferences(this),
            owners = null,
            containers = getContainers(this)
        )
    }

    private fun getContainerStatuses(pod: V1Pod) = pod.status?.containerStatuses

    private fun getOwnerReferences(pod: V1Pod): List<PodOwnerReference> {
        return pod.metadata?.ownerReferences?.map { ref ->
            PodOwnerReference(ref.kind, ref.name)
        } ?: emptyList()
    }

    private fun getContainers(pod: V1Pod): List<PodContainerInfo> {
        val statuses = pod.status?.containerStatuses?.associateBy { it.name } ?: emptyMap()

        return pod.spec?.containers?.map { container ->
            val status = statuses[container.name]
            val ports = container.ports?.map { port ->
                PortInfo(port.containerPort, port.name ?: "", Protocol.parse(port.protocol ?: ""))
            } ?: emptyList()
            PodContainerInfo(
                name = container.name,
                restartsCount = status?.restartCount ?: 0,
                image = container.image?.let { parseImage(it) },
                ports = ports,
                status = status?.let { podStatusMapper.mapContainerStatus(it) } ?: ResourceStatusInfo.UNKNOWN
            )
        } ?: emptyList()
    }

    private fun canNotDiscoverPods(): ApiException {
        return ApiException(ApiErrors.CANT_DISCOVER_PODS, "Cannot discover pods for replica set")
    }
}
