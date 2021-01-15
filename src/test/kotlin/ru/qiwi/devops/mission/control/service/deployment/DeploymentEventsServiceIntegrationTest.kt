package ru.qiwi.devops.mission.control.service.deployment

import io.kubernetes.client.openapi.models.V1Deployment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import ru.qiwi.devops.mission.control.model.event.Event
import ru.qiwi.devops.mission.control.platform.configuration.TestConfig
import ru.qiwi.devops.mission.control.platform.kubernetes.Kubernetes
import ru.qiwi.devops.mission.control.platform.kubernetes.KubernetesBaseIntegrationTest
import ru.qiwi.devops.mission.control.platform.kubernetes.createDeploymentFromResources
import ru.qiwi.devops.mission.control.platform.kubernetes.kubernetes
import ru.qiwi.devops.mission.control.platform.mocks.TestMessagesServiceMock
import ru.qiwi.devops.mission.control.service.k8s.DeploymentJoinedInformer
import ru.qiwi.devops.mission.control.service.namespace.NamespaceServiceFactoryImpl

class DeploymentEventsServiceIntegrationTest : KubernetesBaseIntegrationTest() {
    lateinit var service: DeploymentEventsServiceImpl
    lateinit var deployment: V1Deployment

    @BeforeEach
    fun init() {
        val informer = DeploymentJoinedInformer(Kubernetes.clusters, NamespaceServiceFactoryImpl(Kubernetes.clusters))
        val mapper = DeploymentMapperImpl(TestMessagesServiceMock())
        service = DeploymentEventsServiceImpl(informer, mapper)

        deployment = createDeploymentFromResources("successful")
    }

    @Test
    fun shouldProduceEventWhenDeploymentCreated() {
        val flux = service.getDeploymentEvents()
            .filter { event ->
                event is Event.AddEvent && event.obj.metadata.name == deployment.metadata?.name
            }
            .take(1)

        StepVerifier.create(flux)
            .then {
                Kubernetes.defaultCluster.applyDeployment(deployment)
            }
            .expectNextMatches {
                it is Event.AddEvent && it.obj.metadata.name == deployment.metadata?.name
            }
            .expectComplete()
            .verify(TestConfig.kubernetes.waitForEvent)
    }

    @Test
    fun shouldProduceEventWhenDeploymentUpdated() {
        Kubernetes.defaultCluster.applyDeployment(deployment)

        val flux = service.getDeploymentEvents()
            .filter { event ->
                event is Event.UpdateEvent && event.oldObj.metadata.name == deployment.metadata?.name
            }
            .takeWhile { it is Event.UpdateEvent && it.newObj.replicas != 2 }
            .log()

        StepVerifier.create(flux)
            .then {
                Kubernetes.defaultCluster.applyDeployment(deployment.apply {
                    spec?.apply {
                        replicas = 2
                    }
                })
            }
            .thenConsumeWhile {
                it is Event.UpdateEvent && it.newObj.replicas != 2
            }
            .expectComplete()
            .verify(TestConfig.kubernetes.waitForEvent)
    }

    @Test
    fun shouldProduceEventWhenDeploymentDeleted() {
        Kubernetes.defaultCluster.applyDeployment(deployment)

        val flux = service.getDeploymentEvents()
            .filter { event ->
                event is Event.DeleteEvent && event.obj.metadata.name == deployment.metadata?.name
            }
            .take(1)
            .log()

        StepVerifier.create(flux)
            .then {
                Kubernetes.defaultCluster.deleteDeployment(deployment)
            }
            .expectNextMatches {
                it is Event.DeleteEvent && it.obj.metadata.name == deployment.metadata?.name
            }
            .expectComplete()
            .verify(TestConfig.kubernetes.waitForEvent)
    }
}