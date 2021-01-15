package ru.qiwi.devops.mission.control.service.deployment

import io.kubernetes.client.openapi.models.V1Deployment
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import ru.qiwi.devops.mission.control.model.event.Event
import ru.qiwi.devops.mission.control.platform.kubernetes.createDeployment
import ru.qiwi.devops.mission.control.platform.mocks.InformerMock
import ru.qiwi.devops.mission.control.platform.mocks.TestMessagesServiceMock
import java.time.Duration

class DeploymentEventsServiceTest {
    @Test
    fun shouldProduceEventsWhenDeploymentsCreated() {
        val informer = InformerMock<V1Deployment>()
        val mapper = DeploymentMapperImpl(TestMessagesServiceMock())

        val deploymentsWatcher = DeploymentEventsServiceImpl(informer, mapper)

        StepVerifier.create(deploymentsWatcher.getDeploymentEvents())
            .then {
                informer
                    .produce(Event.AddEvent(createDeployment("a", "a"), "cluster"))
                    .produce(Event.AddEvent(createDeployment("a", "b"), "cluster"))
                    .produce(Event.AddEvent(createDeployment("a", "c"), "cluster"))
                    .close()
            }
            .expectNextMatches { it is Event.AddEvent && it.obj.metadata.name == "a" }
            .expectNextMatches { it is Event.AddEvent && it.obj.metadata.name == "b" }
            .expectNextMatches { it is Event.AddEvent && it.obj.metadata.name == "c" }
            .expectComplete()
            .verify(Duration.ofSeconds(1))
    }
}