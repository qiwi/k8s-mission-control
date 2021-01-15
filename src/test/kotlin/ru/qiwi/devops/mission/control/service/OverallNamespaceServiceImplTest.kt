package ru.qiwi.devops.mission.control.service

import org.junit.jupiter.api.Test
import org.mockito.Mockito
import ru.qiwi.devops.mission.control.model.KubeCluster
import ru.qiwi.devops.mission.control.service.cluster.ClusterService
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient
import ru.qiwi.devops.mission.control.service.namespace.OverallNamespaceServiceImpl
import ru.qiwi.devops.mission.control.utils.createNamespace

class OverallNamespaceServiceImplTest {

    @Test
    fun shouldSkipFailedClusters() {
        val clusterService = Mockito.mock(ClusterService::class.java)

        val aClient = Mockito.mock(KubernetesClient::class.java)
        Mockito.`when`(aClient.listNamespaces()).thenReturn(listOf("A-a", "A-b").map { createNamespace(it) })

        val bClient = Mockito.mock(KubernetesClient::class.java)
        Mockito.`when`(bClient.listNamespaces()).thenReturn(listOf("B-a", "B-b").map { createNamespace(it) })

        val cClient = Mockito.mock(KubernetesClient::class.java)
        Mockito.`when`(cClient.listNamespaces()).thenThrow(RuntimeException("test"))

        Mockito.`when`(clusterService.getClusters()).thenReturn(listOf(
            KubeCluster("A", "", "", "", ""),
            KubeCluster("B", "", "", "", ""),
            KubeCluster("C", "", "", "", "")
        ))
        Mockito.`when`(clusterService.getClusterClient("A")).thenReturn(aClient)
        Mockito.`when`(clusterService.getClusterClient("B")).thenReturn(bClient)
        Mockito.`when`(clusterService.getClusterClient("C")).thenReturn(cClient)

        val service = OverallNamespaceServiceImpl(clusterService)

        service.getUniqueNamespacesInAllClusters()
            .map { it.name }
            .toTypedArray()
            .contentEquals(arrayOf("A-a", "A-b", "B-a", "B-b"))
            // and there should not be namespaces from cluster C
    }
}