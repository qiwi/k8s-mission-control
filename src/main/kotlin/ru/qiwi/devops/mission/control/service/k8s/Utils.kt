package ru.qiwi.devops.mission.control.service.k8s

import io.kubernetes.client.informer.ResourceEventHandler
import ru.qiwi.devops.mission.control.model.event.Event

fun <T> createEventHandler(callback: InformerEventCallback<T>, clusterName: String): ResourceEventHandler<T> {
    return object : ResourceEventHandler<T> {
        override fun onUpdate(oldObj: T, newObj: T) {
            callback(Event.UpdateEvent(oldObj, newObj, clusterName))
        }

        override fun onDelete(obj: T, deletedFinalStateUnknown: Boolean) {
            callback(Event.DeleteEvent(obj, deletedFinalStateUnknown, clusterName))
        }

        override fun onAdd(obj: T) {
            callback(Event.AddEvent(obj, clusterName))
        }
    }
}