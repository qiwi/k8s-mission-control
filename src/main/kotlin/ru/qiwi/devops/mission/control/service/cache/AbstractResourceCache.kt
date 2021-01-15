package ru.qiwi.devops.mission.control.service.cache

import reactor.core.publisher.Flux
import ru.qiwi.devops.mission.control.model.AbstractResource
import ru.qiwi.devops.mission.control.model.event.Event
import ru.qiwi.devops.mission.control.utils.getLogger
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractResourceCache<T>(
    private val flux: Flux<Event<T>>
) where T : AbstractResource {
    private val cache = ConcurrentHashMap<ResourceKey, T>()
    private val logger = getLogger<AbstractResourceCache<*>>()

    fun getAll(): Iterable<T> {
        return cache.values
    }

    fun getByCluster(clusterName: String): Iterable<T> {
        return cache.filter { it.key.clusterName == clusterName }.map { it.value }
    }

    fun getNamespace(clusterName: String, namespace: String): Iterable<T> {
        return cache.filter {
            it.key.clusterName == clusterName &&
                it.key.namespace == namespace
        }.map { it.value }
    }

    fun findOne(clusterName: String, namespace: String, name: String): T? {
        return cache[ResourceKey(clusterName, namespace, name)]
    }

    protected fun start() {
        flux.doOnNext {
            try {
                onEvent(it)
            } catch (e: Throwable) {
                logger.error("Unexpected exception occurred", e)
                throw e
            }
        }.subscribe()
    }

    protected open fun getKey(clusterName: String, resource: T): ResourceKey {
        return ResourceKey(
            clusterName,
            namespace = resource.metadata.namespace,
            name = resource.metadata.name
        )
    }

    protected open fun onEvent(event: Event<T>) {
        when (event) {
            is Event.AddEvent -> {
                val key = getKey(event.clusterName, event.obj)
                cache[key] = event.obj
            }
            is Event.UpdateEvent -> {
                val key = getKey(event.clusterName, event.newObj)
                cache[key] = event.newObj
            }
            is Event.DeleteEvent -> {
                val key = getKey(event.clusterName, event.obj)
                cache.remove(key)
            }
        }
    }

    protected data class ResourceKey(
        val clusterName: String,
        val namespace: String,
        val name: String
    )
}