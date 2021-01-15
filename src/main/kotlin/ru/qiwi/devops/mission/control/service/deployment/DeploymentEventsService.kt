package ru.qiwi.devops.mission.control.service.deployment

import reactor.core.publisher.Flux
import ru.qiwi.devops.mission.control.model.DeploymentInfo
import ru.qiwi.devops.mission.control.model.event.Event

interface DeploymentEventsService {
    fun getDeploymentEvents(): Flux<Event<DeploymentInfo>>
}