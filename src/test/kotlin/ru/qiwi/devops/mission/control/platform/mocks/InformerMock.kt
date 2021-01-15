package ru.qiwi.devops.mission.control.platform.mocks

import ru.qiwi.devops.mission.control.model.event.Event
import ru.qiwi.devops.mission.control.service.k8s.Informer
import ru.qiwi.devops.mission.control.service.k8s.InformerEventCallback

class InformerMock<T> : Informer<T> {
    private val eventCallbacks = mutableListOf<InformerEventCallback<T>>()
    private val closeCallbacks = mutableListOf<() -> Unit>()

    override fun addEventCallback(callback: (Event<T>) -> Unit) {
        eventCallbacks.add(callback)
    }

    override fun addCloseCallback(callback: () -> Unit) {
        closeCallbacks.add(callback)
    }

    fun close() {
        eventCallbacks.clear()
        closeCallbacks.forEach { it() }
    }

    fun produce(event: Event<T>): InformerMock<T> {
        eventCallbacks.forEach { it(event) }
        return this
    }
}