package ru.qiwi.devops.mission.control.platform.kubernetes

import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.models.V1Deployment
import ru.qiwi.devops.mission.control.service.health.cluster.DisabledClusterHealthReceiver
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClientImpl
import ru.qiwi.devops.mission.control.utils.getLogger

class KubernetesHelper(
    val clusterName: String,
    private val apiClient: ApiClient,
    private val clusterConfig: ClusterConfig
) {
    private val logger = getLogger<KubernetesHelper>()
    val deleters = mutableListOf<() -> Unit>()
    val client = KubernetesClientImpl({ apiClient }, clusterName, DisabledClusterHealthReceiver())

    fun applyDeployment(deployment: V1Deployment) {
        val ns = deployment.metadata?.namespace ?: throw IllegalArgumentException("Namespace should be provided")
        val name = deployment.metadata?.name ?: throw IllegalArgumentException("Name should be provided")

        logger.info("Applying deployment $ns/$name to ${clusterConfig.host}...")

        val api = AppsV1Api(apiClient)

        val existsDeployment = client.findDeployment(ns, name)
        logApiExceptions {
            if (existsDeployment == null) {
                api.createNamespacedDeployment(ns, deployment, null, null, null)
            } else {
                api.replaceNamespacedDeployment(name, ns, deployment, null, null, null)
            }
        }

        deleters.add { deleteDeployment(deployment) }
    }

    fun deleteDeployment(deployment: V1Deployment) {
        val ns = deployment.metadata?.namespace ?: throw IllegalArgumentException("Namespace should be provided")
        val name = deployment.metadata?.name ?: throw IllegalArgumentException("Name should be provided")

        logger.info("Deleting deployment $ns/$name from ${clusterConfig.host}...")

        val api = AppsV1Api(apiClient)

        logApiExceptions {
            api.deleteNamespacedDeployment(name, ns, null, null, null, null, null, null)
        }
    }

    fun deleteAllCreatedResources() {
        deleters.forEach {
            try {
                it()
            } catch (e: Exception) {
                logger.error("Can't delete resource", e)
            }
        }
    }

    private fun <T> logApiExceptions(fn: () -> T): T {
        try {
            return fn()
        } catch (e: ApiException) {
            logger.error("Cluster $clusterName returned an error: ${e.code} ${e.message} ${e.responseBody}", e)
            throw e
        } catch (e: Exception) {
            logger.error("Unexpected error occurred while requesting cluster api", e)
            throw e
        }
    }
}