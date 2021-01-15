package ru.qiwi.devops.mission.control.service.namespace

import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.model.NamespaceInfo
import ru.qiwi.devops.mission.control.service.cluster.ClusterService
import ru.qiwi.devops.mission.control.utils.getLogger
import java.util.stream.Stream
import kotlin.streams.toList

@Component
class OverallNamespaceServiceImpl(
    val clusterService: ClusterService
) : OverallNamespaceService {
    private val logger = getLogger<OverallNamespaceServiceImpl>()

    override fun getUniqueNamespacesInAllClusters(): List<NamespaceInfo> {
        logger.debug("Looking for unique namespaces in all clusters...")

        return clusterService.getClusters().parallelStream()
            .flatMap { getNamespacesInCluster(it.name) }
            .map { NamespaceInfo(it) }
            .distinct()
            .toList()
    }

    private fun getNamespacesInCluster(clusterName: String): Stream<String> {
        logger.debug("Looking for namespaces in $clusterName...")
        val client = clusterService.getClusterClient(clusterName)

        return try {
            client!!.listNamespaces()
                .mapNotNull { it.metadata?.name }
                .stream()
        } catch (e: Exception) {
            logger.error("Cannot fetch namespaces from $clusterName", e)
            Stream.empty()
        }
    }
}