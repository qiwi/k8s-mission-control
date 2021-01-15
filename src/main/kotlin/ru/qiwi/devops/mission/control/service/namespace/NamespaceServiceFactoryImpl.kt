package ru.qiwi.devops.mission.control.service.namespace

import org.springframework.stereotype.Service
import ru.qiwi.devops.mission.control.service.cluster.ClusterService

@Service
class NamespaceServiceFactoryImpl(
    private val clusterService: ClusterService
) : NamespaceServiceFactory {
    override fun createNamespaceService(clusterName: String): NamespaceService? {
        return clusterService.getClusterClient(clusterName)
            ?.let { client -> NamespaceServiceImpl(client) }
    }
}