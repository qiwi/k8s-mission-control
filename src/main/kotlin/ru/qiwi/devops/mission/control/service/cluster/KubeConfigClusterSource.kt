package ru.qiwi.devops.mission.control.service.cluster

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.config.ClustersConfig
import ru.qiwi.devops.mission.control.model.KubeCluster
import ru.qiwi.devops.mission.control.service.k8s.KubeClustersParser

@Component
@ConditionalOnProperty("mission-control.clusters-source.type", havingValue = "kubeconfig")
class KubeConfigClusterSource(
    private val config: ClustersConfig
) : KubernetesClusterSource {
    override fun getClusters(): List<KubeCluster> {
        val kubeconfig = requireNotNull(config.clustersSource?.kubeconfig) {
            "mission-control.clusters-source.kubeconfig is required"
        }
        return KubeClustersParser.fromFile(kubeconfig.path)
    }
}