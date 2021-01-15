package ru.qiwi.devops.mission.control.service.cluster

import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.credentials.AccessTokenAuthentication
import org.springframework.stereotype.Service
import ru.qiwi.devops.mission.control.config.ClustersConfig
import ru.qiwi.devops.mission.control.model.KubeCluster
import ru.qiwi.devops.mission.control.service.health.cluster.ClusterHealthMonitorSource
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClientImpl
import ru.qiwi.devops.mission.control.service.security.KubernetesTokenSource
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.utils.getUnsafeOkHttpClient
import java.io.Closeable

@Service
class ClusterServiceImpl(
    private val config: ClustersConfig,
    private val tokenSource: KubernetesTokenSource,
    private val monitorSource: ClusterHealthMonitorSource
) : ClusterService, Closeable {
    private val logger = getLogger<ClusterServiceImpl>()

    private var clusters = getClustersFromConfig()

    private val clients: Map<String, KubernetesClient> = clusters
        .map { Pair(it.name, createClusterClient(it)) }
        .toMap()

    override fun getClusters(): List<KubeCluster> {
        return clusters
    }

    override fun getClusterClient(clusterName: String): KubernetesClient? {
        return clients[clusterName] ?: run {
            logger.warn("Cluster '$clusterName' don't exist")
            return null
        }
    }

    override fun close() {
        clients.values.forEach {
            try {
                it.close()
            } catch (e: Exception) {
                logger.error("Can't close client for cluster ${it.clusterName}", e)
            }
        }
    }

    private fun getClustersFromConfig(): List<KubeCluster> {
        return config.clusters
            .map { cluster ->
                val tokenInfo = config.tokens.singleOrNull { token -> token.name == cluster.name }
                KubeCluster(
                    cluster.name, cluster.displayName, cluster.host, cluster.dc,
                    token = tokenSource.getToken(cluster.tokenName ?: cluster.name) ?: tokenInfo?.token
                        ?: emptyToken(cluster.name)
                )
            }
    }

    private fun emptyToken(clusterName: String): String? {
        logger.error("Can't find token for cluster $clusterName")
        return null
    }

    private fun createClusterClient(kubeCluster: KubeCluster): KubernetesClient {
        logger.info("Creating client for ${kubeCluster.name}...")
        return KubernetesClientImpl(
            clientSource = { createApiClient(kubeCluster) },
            clusterName = kubeCluster.name,
            healthReceiver = monitorSource.getMonitor(kubeCluster.name).getReceiver()
        )
    }

    private fun createApiClient(kubeCluster: KubeCluster): ApiClient {
        return ClientBuilder.standard()
            .setBasePath(kubeCluster.host)
            .let { if (kubeCluster.hasAuthentication()) it.setAuthentication(AccessTokenAuthentication(kubeCluster.token)) else it }
            .build()
            .setHttpClient(getUnsafeOkHttpClient())
    }
}
