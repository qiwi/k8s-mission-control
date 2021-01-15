package ru.qiwi.devops.mission.control.service.deployment

import io.kubernetes.client.openapi.models.V1Deployment
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import ru.qiwi.devops.mission.control.model.DeploymentInfo
import ru.qiwi.devops.mission.control.model.event.Event
import ru.qiwi.devops.mission.control.service.k8s.Informer

@Service
class DeploymentEventsServiceImpl(
    private val informer: Informer<V1Deployment>,
    private val deploymentMapper: DeploymentMapper
) : DeploymentEventsService {
    override fun getDeploymentEvents(): Flux<Event<DeploymentInfo>> {
        return informer.createFlux()
            .map { event -> event.map { d -> deploymentMapper.mapDeployment(d, event.clusterName) } }
    }
}