package ru.qiwi.devops.mission.control.messages

object PodMessages {
    fun MessagesService.containerIsNotReady(container: String, reason: String) =
        this.create("messages.pod.containerIsNotReady", "container" to container, "reason" to reason)

    fun MessagesService.containerHasTerminatedInFailure(container: String, reason: String) =
        this.create("messages.pod.containerHasTerminatedInFailure", "container" to container, "reason" to reason)

    fun MessagesService.containerIsInUnreadyState(container: String) =
        this.create("messages.pod.containerIsInUnreadyState", "container" to container)

    fun MessagesService.containerIsInUnknownState(container: String) =
        this.create("messages.pod.containerIsInUnknownState", "container" to container)

    fun MessagesService.containerHasBeenRestartedNTimes(container: String, times: Int) =
        this.create("messages.pod.containerHasBeenRestartedNTimes", "container" to container, "times" to times.toString())

    fun MessagesService.containerHasTerminatedInFailureWithExitCode(container: String, exitCode: Int) =
        this.create("messages.pod.containerHasTerminatedInFailureWithExitCode", "container" to container, "exitCode" to exitCode.toString())

    fun MessagesService.containerHasFailedToStartWithExitCode(container: String, exitCode: Int) =
        this.create("messages.pod.containerHasFailedToStartWithExitCode", "container" to container, "exitCode" to exitCode.toString())

    fun MessagesService.containerHasFailedToStart(container: String) =
        this.create("messages.pod.containerHasFailedToStart", "container" to container)

    fun MessagesService.containerHasFailedToDownloadImage(container: String, image: String) =
        this.create("messages.pod.containerHasFailedToDownloadImage", "container" to container, "image" to image)

    fun MessagesService.podIsNotReady(reason: String) =
        this.create("messages.pod.podIsNotReady", "reason" to reason)

    fun MessagesService.podHasAtLeastOneUnreadyContainer(reason: String) =
        this.create("messages.pod.podHasAtLeastOneUnreadyContainer", "reason" to reason)

    fun MessagesService.podHasNotBeenScheduled(reason: String) =
        this.create("messages.pod.podHasNotBeenScheduled", "reason" to reason)

    fun MessagesService.podHasNotBeenInitialized(reason: String) =
        this.create("messages.pod.podHasNotBeenInitialized", "reason" to reason)

    fun MessagesService.podDoesNotHaveCondition(condition: String) =
        this.create("messages.pod.podDoesNotHaveCondition", "condition" to condition)

    fun MessagesService.allContainersHaveTerminatedInSuccess() =
        this.create("messages.pod.allContainersHaveTerminatedInSuccess")
}