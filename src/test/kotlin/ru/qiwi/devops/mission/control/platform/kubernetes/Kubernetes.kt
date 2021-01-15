package ru.qiwi.devops.mission.control.platform.kubernetes

import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.credentials.AccessTokenAuthentication
import ru.qiwi.devops.mission.control.model.KubeCluster
import ru.qiwi.devops.mission.control.platform.configuration.TestConfig
import ru.qiwi.devops.mission.control.service.cluster.ClusterService
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient
import ru.qiwi.devops.mission.control.utils.getUnsafeOkHttpClient

object Kubernetes {
    private val config = TestConfig.kubernetes

    private val helpers = config.clusters.map {
        it.name to createCluster(it)
    }.toMap()

    val defaultCluster: KubernetesHelper
        get() = config.defaultCluster?.let { getCluster(it) }
                    ?: throw IllegalStateException("Default cluster is not defined in configuration")

    val clusters = object : ClusterService {
        override fun getClusters(): List<KubeCluster> {
            return helpers.values.map {
                KubeCluster(it.clusterName, it.clusterName, "", "", "")
            }
        }

        override fun getClusterClient(clusterName: String): KubernetesClient? {
            return helpers[clusterName]?.client
        }
    }

    private fun getCluster(name: String): KubernetesHelper {
        return helpers[name]
            ?: throw IllegalStateException("Cluster $name not found in test configuration")
    }

    fun deleteAllCreatedResources() {
        helpers.values.forEach {
            it.deleteAllCreatedResources()
        }
    }

    private fun createCluster(clusterConfig: ClusterConfig): KubernetesHelper {
        val apiClient = ClientBuilder.standard()
            .setBasePath(clusterConfig.host)
            .let { it.setAuthentication(AccessTokenAuthentication(clusterConfig.token)) }
            .build()
            .setHttpClient(getUnsafeOkHttpClient())
        return KubernetesHelper(clusterConfig.name, apiClient, clusterConfig)
    }
}