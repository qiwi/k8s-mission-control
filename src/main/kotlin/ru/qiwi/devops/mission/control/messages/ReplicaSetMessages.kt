package ru.qiwi.devops.mission.control.messages

object ReplicaSetMessages {
    fun MessagesService.lessPodsAreAvailableThanDesired(available: Int, desired: Int) =
        this.create("messages.rs.lessPodsAreAvailableThanDesired", "available" to available.toString(), "desired" to desired.toString())
}