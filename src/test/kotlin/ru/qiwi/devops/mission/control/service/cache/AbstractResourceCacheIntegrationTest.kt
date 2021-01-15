package ru.qiwi.devops.mission.control.service.cache

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.qiwi.devops.mission.control.model.DeploymentInfo
import ru.qiwi.devops.mission.control.platform.kubernetes.Kubernetes
import ru.qiwi.devops.mission.control.platform.kubernetes.KubernetesBaseIntegrationTest
import ru.qiwi.devops.mission.control.platform.kubernetes.createDeploymentFromResources
import ru.qiwi.devops.mission.control.platform.mocks.TestMessagesServiceMock
import ru.qiwi.devops.mission.control.service.deployment.DeploymentEventsServiceImpl
import ru.qiwi.devops.mission.control.service.deployment.DeploymentMapperImpl
import ru.qiwi.devops.mission.control.service.k8s.DeploymentJoinedInformer
import ru.qiwi.devops.mission.control.service.namespace.NamespaceServiceFactoryImpl

class AbstractResourceCacheIntegrationTest : KubernetesBaseIntegrationTest() {
    lateinit var cache: AbstractResourceCache<DeploymentInfo>

    @BeforeEach
    fun init() {
        val informer = DeploymentJoinedInformer(Kubernetes.clusters, NamespaceServiceFactoryImpl(Kubernetes.clusters))
        val mapper = DeploymentMapperImpl(TestMessagesServiceMock())
        val service = DeploymentEventsServiceImpl(informer, mapper)

        cache = object : AbstractResourceCache<DeploymentInfo>(service.getDeploymentEvents()) {}
    }

    @Test
    fun shouldContainNewResourceWhenItCreated() {
        val deployment = createDeploymentFromResources("successful")
            .apply { Kubernetes.defaultCluster.applyDeployment(this) }

        Thread.sleep(200)

        val found = cache.findOne(
            Kubernetes.defaultCluster.clusterName,
            deployment.metadata?.namespace ?: "",
            deployment.metadata?.name ?: "")

        assertThat(found).isNotNull()
    }

    @Test
    fun shouldContainUpdatedResourceWhenItUpdated() {
        val deployment = createDeploymentFromResources("successful")
            .apply { Kubernetes.defaultCluster.applyDeployment(this) }

        Thread.sleep(200)

        val found1 = cache.findOne(
            Kubernetes.defaultCluster.clusterName,
            deployment.metadata?.namespace ?: "",
            deployment.metadata?.name ?: "")

        assertThat(found1).isNotNull()
        assertThat(found1?.replicas).isEqualTo(1)

        Kubernetes.defaultCluster.applyDeployment(deployment.apply {
            spec?.apply {
                replicas = 2
            }
        })

        Thread.sleep(200)

        val found2 = cache.findOne(
            Kubernetes.defaultCluster.clusterName,
            deployment.metadata?.namespace ?: "",
            deployment.metadata?.name ?: "")

        assertThat(found2).isNotNull()
        assertThat(found2?.replicas).isEqualTo(2)
    }

    @Test
    fun shouldNotContainUpdatedResourceWhenItDeleted() {
        val deployment = createDeploymentFromResources("successful")
            .apply { Kubernetes.defaultCluster.applyDeployment(this) }

        Thread.sleep(200)

        val found1 = cache.findOne(
            Kubernetes.defaultCluster.clusterName,
            deployment.metadata?.namespace ?: "",
            deployment.metadata?.name ?: "")

        assertThat(found1).isNotNull()
        assertThat(found1?.replicas).isEqualTo(1)

        Kubernetes.defaultCluster.deleteDeployment(deployment)

        Thread.sleep(200)

        val found2 = cache.findOne(
            Kubernetes.defaultCluster.clusterName,
            deployment.metadata?.namespace ?: "",
            deployment.metadata?.name ?: "")

        assertThat(found2).isNull()
    }
}