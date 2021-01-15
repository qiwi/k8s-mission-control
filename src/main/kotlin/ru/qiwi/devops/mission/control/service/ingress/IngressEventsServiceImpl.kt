package ru.qiwi.devops.mission.control.service.ingress

import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import ru.qiwi.devops.mission.control.model.event.Event
import ru.qiwi.devops.mission.control.model.ingress.IngressInfo
import ru.qiwi.devops.mission.control.model.ingress.toIngressInfo
import ru.qiwi.devops.mission.control.service.k8s.Informer

@Service
class IngressEventsServiceImpl(
    private val informer: Informer<ExtensionsV1beta1Ingress>
) : IngressEventsService {
    override fun getIngressEvents(): Flux<Event<IngressInfo>> {
        return informer.createFlux()
            .map { event -> event.map { i -> i.toIngressInfo(event.clusterName) } }
    }
}