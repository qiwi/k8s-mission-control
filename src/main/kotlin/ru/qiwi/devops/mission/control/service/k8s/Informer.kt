package ru.qiwi.devops.mission.control.service.k8s

import reactor.core.publisher.Flux
import ru.qiwi.devops.mission.control.model.event.Event

interface Informer<T> {
    fun addEventCallback(callback: InformerEventCallback<T>)

    fun addCloseCallback(callback: () -> Unit)

    fun createFlux(): Flux<Event<T>> {
        return Flux.create { sink ->
            this.addCloseCallback {
                sink.complete()
            }
            this.addEventCallback {
                sink.next(it)
            }
        }
    }
}

typealias InformerEventCallback<T> = (Event<T>) -> Unit