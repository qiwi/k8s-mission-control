package ru.qiwi.devops.mission.control.service.cluster

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.config.ClustersConfig
import ru.qiwi.devops.mission.control.model.KubeCluster
import ru.qiwi.devops.mission.control.utils.getLogger

@Component
@ConditionalOnProperty("mission-control.clusters-source.type", havingValue = "appconfig", matchIfMissing = true)
class AppConfigClusterSource(
    private val config: ClustersConfig
) : KubernetesClusterSource {
    private val logger = getLogger<AppConfigClusterSource>()

    override fun getClusters(): List<KubeCluster> {
        return config.clusters
            .map { cluster ->
                val token = config.tokens.singleOrNull { token -> token.name == cluster.tokenName }
                    ?.token ?: emptyToken(cluster.name)
                KubeCluster(
                    name = cluster.name,
                    displayName = cluster.displayName,
                    host = cluster.host,
                    dataCenter = cluster.dc,
                    token = token
                )
            }
    }

    private fun emptyToken(clusterName: String): String? {
        logger.error("Can't find token for cluster $clusterName")
        return null
    }
}