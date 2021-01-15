package ru.qiwi.devops.mission.control.service.deployment

import org.junit.jupiter.api.Test
import org.mockito.Mockito
import ru.qiwi.devops.mission.control.model.NamespaceInfo
import ru.qiwi.devops.mission.control.platform.mocks.TestMessagesServiceMock
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient
import ru.qiwi.devops.mission.control.service.namespace.NamespaceService

class DeploymentServiceImplTest {

    @Test
    fun deploymentServiceShouldCallForDeploymentsSeparatelyForEachNamespace() {
        val mapper = DeploymentMapperImpl(TestMessagesServiceMock())

        val client = Mockito.mock(KubernetesClient::class.java)

        val namespaces = Mockito.mock(NamespaceService::class.java)
        Mockito.`when`(namespaces.getAccessibleNamespaces()).thenReturn(listOf("A", "B", "C").map { NamespaceInfo(it) })

        val service = DeploymentServiceImpl(client, namespaces, mapper)

        service.getDeployments(null)

        Mockito.verify(client).listDeployments("A")
        Mockito.verify(client).listDeployments("B")
        Mockito.verify(client).listDeployments("C")
    }
}