package ru.qiwi.devops.mission.control.service.ingress

import reactor.core.publisher.Flux
import ru.qiwi.devops.mission.control.model.event.Event
import ru.qiwi.devops.mission.control.model.ingress.IngressInfo

interface IngressEventsService {
    fun getIngressEvents(): Flux<Event<IngressInfo>>
}