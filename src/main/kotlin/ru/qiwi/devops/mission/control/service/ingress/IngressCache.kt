package ru.qiwi.devops.mission.control.service.ingress

import org.springframework.stereotype.Service
import ru.qiwi.devops.mission.control.model.event.Event
import ru.qiwi.devops.mission.control.model.ingress.IngressInfo
import ru.qiwi.devops.mission.control.service.cache.AbstractResourceCache
import java.util.concurrent.ConcurrentHashMap

@Service
class IngressCache(
    events: IngressEventsService
) : AbstractResourceCache<IngressInfo>(events.getIngressEvents()) {
    private val byService = ConcurrentHashMap<ResourceKey, List<ResourceKey>>()

    init {
        start()
    }

    fun getByService(clusterName: String, namespace: String, serviceName: String): List<IngressInfo> {
        return byService[ResourceKey(clusterName, namespace, serviceName)]
            ?.mapNotNull { key -> findOne(key.clusterName, key.namespace, key.name) }
            ?: emptyList()
    }

    override fun onEvent(event: Event<IngressInfo>) {
        super.onEvent(event)

        when (event) {
            is Event.AddEvent -> {
                val ingressKey = getKey(event.clusterName, event.obj)
                event.obj.getServices(event.clusterName)
                    .forEach { serviceKey ->
                        byService.compute(serviceKey) { _, _ ->
                            listOf(ingressKey)
                        }
                    }
            }
            else -> { }
        }
    }

    private fun IngressInfo.getServices(clusterName: String): List<ResourceKey> {
        return this.rules.flatMap { rule ->
            rule.http.map { http -> ResourceKey(clusterName, this.metadata.namespace, http.backend.serviceName) }
        }
    }
}