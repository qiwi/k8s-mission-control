package ru.qiwi.devops.mission.control.service.replicaset

import io.kubernetes.client.openapi.models.V1ReplicaSet
import ru.qiwi.devops.mission.control.messages.MessagesService
import ru.qiwi.devops.mission.control.messages.ReplicaSetMessages.lessPodsAreAvailableThanDesired
import ru.qiwi.devops.mission.control.model.ImageInfo
import ru.qiwi.devops.mission.control.model.LabelInfo
import ru.qiwi.devops.mission.control.model.ReplicaSetInfo
import ru.qiwi.devops.mission.control.model.ResourceStatusInfo
import ru.qiwi.devops.mission.control.model.ResourceStatusLevel
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient
import ru.qiwi.devops.mission.control.service.parseImage
import ru.qiwi.devops.mission.control.service.pod.PodServiceImpl
import ru.qiwi.devops.mission.control.service.toMetadataInfo
import ru.qiwi.devops.mission.control.utils.getLogger

class ReplicaSetServiceImpl(
    private val client: KubernetesClient,
    private val messagesService: MessagesService
) : ReplicaSetService {

    private val logger = getLogger<PodServiceImpl>()

    override fun getReplicaSetsByDeployment(namespace: String, deploymentName: String): List<ReplicaSetInfo> {
        // TODO optimize searching of dependent replicasets
        return client.listReplicaSets(namespace)
            .filter { it.isOwnedByDeployment(deploymentName) }
            .map { it.toReplicaSetInfo() }
    }

    override fun findReplicaSet(namespace: String, replicaSetName: String): ReplicaSetInfo? {
        logger.info("Looking for replicaSet $namespace/$replicaSetName in ${client.clusterName}...")

        return client.findReplicaSet(namespace, replicaSetName)?.toReplicaSetInfo() ?: run {
            logger.info("ReplicaSet $namespace/$replicaSetName not found")
            null
        }
    }

    private fun V1ReplicaSet.toReplicaSetInfo() = ReplicaSetInfo(
        metadata = this.metadata.toMetadataInfo(client.clusterName),
        images = this.getImages(),
        status = this.getStatusInfo(),
        podSelectionLabels = this.spec?.selector?.matchLabels?.map { LabelInfo(it.key, it.value) },
        desiredPods = this.status?.replicas ?: 0,
        availablePods = this.status?.availableReplicas ?: 0
    )

    private fun V1ReplicaSet.isOwnedByDeployment(deploymentName: String): Boolean {
        return this.metadata?.ownerReferences?.any {
            it.kind == "Deployment" && it.name == deploymentName
        } ?: false
    }

    private fun V1ReplicaSet.getImages(): List<ImageInfo> {
        return this.spec?.template?.spec?.containers
            ?.map { parseImage(it.image.orEmpty()) }
            ?: emptyList()
    }

    private fun V1ReplicaSet.getStatusInfo(): ResourceStatusInfo {
        return this.status?.let {
            val desired = it.replicas
            val available = it.availableReplicas ?: 0
            when {
                desired == 0 -> ResourceStatusInfo(ResourceStatusLevel.INACTIVE)
                desired > available -> ResourceStatusInfo.build {
                    error(messagesService.lessPodsAreAvailableThanDesired(available, desired))
                }
                else -> ResourceStatusInfo.OK
            }
        } ?: ResourceStatusInfo(ResourceStatusLevel.INACTIVE)
    }
}