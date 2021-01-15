package ru.qiwi.devops.mission.control.service.deployment

import org.springframework.stereotype.Service
import ru.qiwi.devops.mission.control.model.DeploymentInfo
import ru.qiwi.devops.mission.control.service.cache.AbstractResourceCache

@Service
class DeploymentCache(
    events: DeploymentEventsService
) : AbstractResourceCache<DeploymentInfo>(events.getDeploymentEvents()) {
    init {
        start()
    }
}