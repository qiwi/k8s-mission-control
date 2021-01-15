package ru.qiwi.devops.mission.control.service.namespace

import ru.qiwi.devops.mission.control.model.NamespaceInfo
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient
import ru.qiwi.devops.mission.control.utils.getLogger

class NamespaceServiceImpl(
    private val client: KubernetesClient
) : NamespaceService {
    private val logger = getLogger<NamespaceServiceImpl>()

    override fun getAccessibleNamespaces(): List<NamespaceInfo> {
        logger.info("Looking for accessible namespaces in cluster ${client.clusterName}...")

        val namespaces = client.listNamespaces().map {
            NamespaceInfo(
                name = it.metadata?.name ?: throw IllegalStateException("Namespace without name has been received")
            )
        }.filter {
            client.checkNamespaceIsAccessible(it.name)
        }

        logger.info("Found ${namespaces.size} namespaces: $namespaces")
        return namespaces
    }
}