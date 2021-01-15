package ru.qiwi.devops.mission.control.messages

object DeploymentMessages {
    fun MessagesService.deploymentDoesNotHaveCondition(condition: String) =
        this.create("messages.deployment.deploymentDoesNotHaveCondition", "condition" to condition)

    fun MessagesService.deploymentIsInProgressingState(reason: String) =
        this.create("messages.deployment.deploymentIsInProgressingState", "reason" to reason)

    fun MessagesService.deploymentIsNotInAvailableState(reason: String) =
        this.create("messages.deployment.deploymentIsNotInAvailableState", "reason" to reason)
}