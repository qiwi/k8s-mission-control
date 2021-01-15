package ru.qiwi.devops.mission.control.service.deployment

import ru.qiwi.devops.mission.control.model.DeploymentInfo
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient
import ru.qiwi.devops.mission.control.service.namespace.NamespaceService
import ru.qiwi.devops.mission.control.utils.getLogger

class DeploymentServiceImpl(
    private val client: KubernetesClient,
    private val namespaceService: NamespaceService,
    private val deploymentMapper: DeploymentMapper
) : DeploymentService {

    private val logger = getLogger<DeploymentServiceImpl>()

    override fun getDeployments(deploymentCondition: String?): List<DeploymentInfo> {
        logger.info("Looking for deployments in cluster ${client.clusterName} in all namespaces...")

        val namespaces = namespaceService.getAccessibleNamespaces().map { it.name }

        return namespaces.flatMap { namespace ->
            logger.debug("Looking for deployments in namespace $namespace...")

            client.listDeployments(namespace)
                .map { deploymentMapper.mapDeployment(it, client.clusterName) }
                .filter {
                    it.metadata.name.contains(deploymentCondition.orEmpty(), ignoreCase = true)
                }
        }
    }

    override fun findDeployment(namespace: String, deploymentName: String): DeploymentInfo? {
        logger.info("Looking for deployment $namespace/$deploymentName...")

        return client.findDeployment(namespace, deploymentName)?.let { deploymentMapper.mapDeployment(it, client.clusterName) } ?: run {
            logger.info("Deployment $namespace/$deploymentName not found")
            null
        }
    }

    override fun getDeploymentsByNamespace(namespace: String): List<DeploymentInfo> {
        logger.info("Looking for deployments in $namespace...")

        return client
            .listDeployments(namespace)
            .map { deploymentMapper.mapDeployment(it, client.clusterName) }
    }
}