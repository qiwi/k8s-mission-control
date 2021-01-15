package ru.qiwi.devops.mission.control.model.event

sealed class Event<T> {
    abstract val clusterName: String

    data class AddEvent<T>(val obj: T, override val clusterName: String) : Event<T>()

    data class UpdateEvent<T>(val oldObj: T, val newObj: T, override val clusterName: String) : Event<T>()

    data class DeleteEvent<T>(val obj: T, val deletedFinalStateUnknown: Boolean, override val clusterName: String) : Event<T>()

    fun <U> map(mapper: (T) -> U): Event<U> {
        return when (this) {
            is AddEvent -> AddEvent(mapper(obj), clusterName)
            is UpdateEvent -> UpdateEvent(mapper(oldObj), mapper(newObj), clusterName)
            is DeleteEvent -> DeleteEvent(mapper(obj), deletedFinalStateUnknown, clusterName)
        }
    }
}