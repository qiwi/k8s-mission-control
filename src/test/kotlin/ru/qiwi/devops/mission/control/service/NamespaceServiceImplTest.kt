package ru.qiwi.devops.mission.control.service

import org.junit.jupiter.api.Test
import org.mockito.Mockito
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient
import ru.qiwi.devops.mission.control.service.namespace.NamespaceServiceImpl
import ru.qiwi.devops.mission.control.utils.createNamespace

class NamespaceServiceImplTest {

    @Test
    fun shouldReturnOnlyAccessibleNamespaces() {
        val client = Mockito.mock(KubernetesClient::class.java)
        Mockito.`when`(client.listNamespaces()).thenReturn(listOf("A", "B", "C").map { createNamespace(it) })
        Mockito.`when`(client.checkNamespaceIsAccessible("A")).thenReturn(true)
        Mockito.`when`(client.checkNamespaceIsAccessible("B")).thenReturn(false)
        Mockito.`when`(client.checkNamespaceIsAccessible("C")).thenReturn(true)

        val service = NamespaceServiceImpl(client)

        service.getAccessibleNamespaces()
            .map { it.name }
            .toTypedArray()
            .contentEquals(arrayOf("A", "C"))
    }

    @Test
    fun shouldCheckEveryNamespaceForAccessibility() {
        val client = Mockito.mock(KubernetesClient::class.java)
        Mockito.`when`(client.listNamespaces()).thenReturn(listOf("A", "B", "C").map { createNamespace(it) })

        val service = NamespaceServiceImpl(client)

        service.getAccessibleNamespaces()

        Mockito.verify(client).checkNamespaceIsAccessible("A")
        Mockito.verify(client).checkNamespaceIsAccessible("B")
        Mockito.verify(client).checkNamespaceIsAccessible("C")
    }
}