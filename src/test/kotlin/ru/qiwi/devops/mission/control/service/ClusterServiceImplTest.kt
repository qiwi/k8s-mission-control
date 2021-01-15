package ru.qiwi.devops.mission.control.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.qiwi.devops.mission.control.service.cluster.ClusterService

@SpringBootTest
class ClusterServiceImplTest {

    @Autowired
    lateinit var clusterService: ClusterService

    @Test
    fun testGetClustersFromConfig() {
        val clusters = clusterService.getClusters()
        assertThat(clusters.size).isEqualTo(1)

        val cluster = clusters.first()
        assertThat(cluster.name).isEqualTo("docker-desktop")
        assertThat(cluster.host).isEqualTo("https://kubernetes.docker.internal:6443")
        assertThat(cluster.dataCenter).isEqualTo("localhost")
        assertThat(cluster.token).isNotNull()
    }

    @Test
    fun testGetClusterClient() {
        val client = clusterService.getClusterClient("docker-desktop")

        assertThat(client).isNotNull()
        assertThat(client?.basePath).isEqualTo("https://kubernetes.docker.internal:6443")
        assertThat(client?.authentications).isNotNull()
    }

    fun testGetClusterClientWhenNotFound() {
        val client = clusterService.getClusterClient("not found cluster")
        assertThat(client).isNull()
    }
}