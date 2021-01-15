package ru.qiwi.devops.mission.control.service.k8s

import io.kubernetes.client.informer.SharedInformer
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api
import io.kubernetes.client.openapi.auth.Authentication
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressList
import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.openapi.models.V1DeploymentList
import io.kubernetes.client.openapi.models.V1Namespace
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.openapi.models.V1ReplicaSet
import io.kubernetes.client.openapi.models.V1Service
import org.apache.http.HttpStatus
import ru.qiwi.devops.mission.control.service.health.cluster.ClusterHealthReceiver
import ru.qiwi.devops.mission.control.utils.apiUnavailableException
import ru.qiwi.devops.mission.control.utils.getLogger
import java.io.IOException
import java.io.InputStream

class KubernetesClientImpl(
    clientSource: () -> ApiClient,
    override val clusterName: String,
    healthReceiver: ClusterHealthReceiver
) : KubernetesClient {
    private val logger = getLogger<KubernetesClientImpl>()

    private val defaultClient = clientSource()

    private val informerFactory = CustomInformerFactory(
        clusterName = clusterName,
        apiClient = clientSource().setReadTimeout(0),
        healthReceiver = healthReceiver
    )
    private val appsApiClient = AppsV1Api(defaultClient)
    private val coreApiClient = CoreV1Api(defaultClient)
    private val extApiClient = ExtensionsV1beta1Api(defaultClient)

    override val authentications: MutableMap<String, Authentication> = defaultClient.authentications
    override val basePath: String = defaultClient.basePath

    override fun createDeploymentInformer(namespace: String): SharedInformer<V1Deployment> {
        return informerFactory.create<V1Deployment, V1DeploymentList>({ params ->
            appsApiClient.listNamespacedDeploymentCall(
                namespace, null, null, null, null, null, null,
                params.resourceVersion, params.timeoutSeconds, params.watch,
                null
            )
        })
    }

    override fun createIngressInformer(namespace: String): SharedInformer<ExtensionsV1beta1Ingress> {
        return informerFactory.create<ExtensionsV1beta1Ingress, ExtensionsV1beta1IngressList>({ params ->
            extApiClient.listNamespacedIngressCall(
                namespace, null, null, null, null, null, null,
                params.resourceVersion, params.timeoutSeconds, params.watch,
                null
            )
        })
    }

    override fun listDeployments(): Iterable<V1Deployment> {
        return logApiExceptions {
            appsApiClient.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, false).items
        }
    }

    override fun listDeployments(namespace: String): Iterable<V1Deployment> {
        return logApiExceptions {
            appsApiClient.listNamespacedDeployment(namespace, null, null, null, null, null, null, null, null, false).items
        }
    }

    override fun listReplicaSets(namespace: String): Iterable<V1ReplicaSet> {
        return logApiExceptions {
            appsApiClient.listNamespacedReplicaSet(namespace, null, null, null, null, null, null, null, null, false).items
        }
    }

    override fun findReplicaSet(namespace: String, replicaSetName: String): V1ReplicaSet? {
        return logApiExceptionsExcept404 {
            appsApiClient.readNamespacedReplicaSet(replicaSetName, namespace, null, false, false)
        }
    }

    override fun listNamespaces(): Iterable<V1Namespace> {
        return logApiExceptions {
            coreApiClient.listNamespace(null, null, null, null, null, null, null, null, false).items
        }
    }

    override fun checkNamespaceIsAccessible(namespace: String): Boolean {
        try {
            coreApiClient.readNamespace(namespace, null, false, false)
            return true
        } catch (e: ApiException) {
            if (e.code == 404 || e.code == 403) {
                logger.debug("Namespace $namespace isn't accessible because cluster $clusterName returned an error: ${e.code} ${e.message} ${e.responseBody}", e)
                return false
            }
            logger.error("Cluster $clusterName returned an error: ${e.code} ${e.message} ${e.responseBody}", e)
            throw e
        } catch (e: Exception) {
            logger.error("Unexpected error occurred while requesting cluster api", e)
            throw e
        }
    }

    override fun findDeployment(namespace: String, deploymentName: String): V1Deployment? {
        return logApiExceptionsExcept404 {
            appsApiClient.readNamespacedDeployment(deploymentName, namespace, null, false, false)
        }
    }

    override fun findService(namespace: String, serviceName: String): V1Service? {
        return logApiExceptionsExcept404 {
            coreApiClient.readNamespacedService(serviceName, namespace, null, false, false)
        }
    }

    override fun listPodsBySelector(namespace: String, labelSelector: String): Iterable<V1Pod> {
        return logApiExceptions {
            coreApiClient.listNamespacedPod(namespace, null, null, null, null, labelSelector, null, null, null, false).items
        }
    }

    override fun findPod(namespace: String, podName: String): V1Pod? {
        return logApiExceptionsExcept404 {
            coreApiClient.readNamespacedPod(podName, namespace, null, false, false)
        }
    }

    override fun listDeploymentPods(namespace: String, deploymentName: String): Iterable<V1Pod> {
        return logApiExceptions {
            coreApiClient.listNamespacedPod(namespace, "false", null, null, null, null, null, null, null, null)
                .items.filter { item -> item.metadata?.name == deploymentName } // TODO search for pods via replicasets
        }
    }

    override fun close() {
        informerFactory.close()
    }

    override fun getNamespacedPodLog(namespace: String, pod: String, containerName: String, sinceSecond: Int, follow: Boolean): String? {
        logger.info("Looking for logs for $pod...")
        return logApiExceptions {
            val response = coreApiClient.readNamespacedPodLogWithHttpInfo(
                pod,
                namespace,
                containerName,
                follow,
                null,
                null,
                false,
                sinceSecond,
                null,
                true
            )
            if (response.statusCode == HttpStatus.SC_OK) {
                response.data
            } else {
                logger.warn("Logs are not available now. Response status: ${response.statusCode}")
                null
            }
        }
    }

    @Throws(ApiException::class, IOException::class)
    override fun streamNamespacedPodLog(
        namespace: String,
        podName: String,
        container: String,
        sinceSeconds: Int,
        follow: Boolean
    ): InputStream {
        val call = coreApiClient.readNamespacedPodLogCall(podName, namespace, container, follow, null, "false", false, sinceSeconds,
            null, true, null
        )
        val result = call.execute()
        if (result.isSuccessful) {
            return result.body!!.byteStream()
        } else {
            throw ApiException("Can't fetch logs: error ${result.code} has been returned from the server")
        }
    }

    private fun <T> logApiExceptionsExcept404(fn: () -> T): T? {
        try {
            return fn()
        } catch (e: ApiException) {
            if (e.code == 404) {
                // Resource not found
                return null
            }

            logger.error("Cluster $clusterName returned an error: ${e.code} ${e.message} ${e.responseBody}", e)
            throw apiUnavailableException(e.message ?: "", e)
        } catch (e: Exception) {
            logger.error("Unexpected error occurred while requesting cluster api", e)
            throw apiUnavailableException("unexpected error", e)
        }
    }

    private fun <T> logApiExceptions(fn: () -> T): T {
        try {
            return fn()
        } catch (e: ApiException) {
            logger.error("Cluster $clusterName returned an error: ${e.code} ${e.message} ${e.responseBody}", e)
            throw apiUnavailableException(e.message ?: "", e)
        } catch (e: Exception) {
            logger.error("Unexpected error occurred while requesting cluster api", e)
            throw apiUnavailableException("unexpected error", e)
        }
    }
}