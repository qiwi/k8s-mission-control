package ru.qiwi.devops.mission.control.service.pod

import io.kubernetes.client.openapi.models.V1ContainerStatus
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.openapi.models.V1PodCondition
import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.messages.MessagesService
import ru.qiwi.devops.mission.control.messages.PodMessages.allContainersHaveTerminatedInSuccess
import ru.qiwi.devops.mission.control.messages.PodMessages.containerHasFailedToDownloadImage
import ru.qiwi.devops.mission.control.messages.PodMessages.containerHasFailedToStart
import ru.qiwi.devops.mission.control.messages.PodMessages.containerHasFailedToStartWithExitCode
import ru.qiwi.devops.mission.control.messages.PodMessages.containerHasTerminatedInFailure
import ru.qiwi.devops.mission.control.messages.PodMessages.containerHasTerminatedInFailureWithExitCode
import ru.qiwi.devops.mission.control.messages.PodMessages.containerIsInUnknownState
import ru.qiwi.devops.mission.control.messages.PodMessages.containerIsInUnreadyState
import ru.qiwi.devops.mission.control.messages.PodMessages.containerIsNotReady
import ru.qiwi.devops.mission.control.messages.PodMessages.podDoesNotHaveCondition
import ru.qiwi.devops.mission.control.messages.PodMessages.podHasAtLeastOneUnreadyContainer
import ru.qiwi.devops.mission.control.messages.PodMessages.podHasNotBeenInitialized
import ru.qiwi.devops.mission.control.messages.PodMessages.podHasNotBeenScheduled
import ru.qiwi.devops.mission.control.messages.PodMessages.podIsNotReady
import ru.qiwi.devops.mission.control.model.ResourceStatusInfo
import ru.qiwi.devops.mission.control.model.ResourceStatusMessageClarity

@Component
class PodStatusMapperImpl(
    private val messagesService: MessagesService
) : PodStatusMapper {
    override fun mapStatus(pod: V1Pod): ResourceStatusInfo {
        val phase = pod.status?.phase

        if (phase == "Succeeded") {
            return ResourceStatusInfo.build {
                info(messagesService.allContainersHaveTerminatedInSuccess())
            }
        }

        return ResourceStatusInfo.build {
            val podScheduled = pod.getCondition("PodScheduled")
            when {
                podScheduled == null -> {
                    error(messagesService.podDoesNotHaveCondition("PodScheduled"))
                }
                podScheduled.status != "True" -> {
                    error(messagesService.podHasNotBeenScheduled(podScheduled.message ?: ""))
                }
            }

            val initialized = pod.getCondition("Initialized")
            when {
                initialized == null -> {
                    error(messagesService.podDoesNotHaveCondition("Initialized"))
                }
                initialized.status != "True" -> {
                    error(messagesService.podHasNotBeenInitialized(initialized.message ?: ""))
                }
            }

            val ready = pod.getCondition("Ready")
            if (ready == null) {
                error(messagesService.podDoesNotHaveCondition("Ready"))
            }

            val containersReady = pod.getCondition("ContainersReady")
            when {
                containersReady == null -> {
                    error(messagesService.podDoesNotHaveCondition("ContainersReady"))
                }
                containersReady.status != "True" -> {
                    error(messagesService.podHasAtLeastOneUnreadyContainer(containersReady.message ?: ""))
                }
                ready?.status != "True" -> {
                    error(messagesService.podIsNotReady(ready?.message ?: ""))
                }
            }

            pod.status?.containerStatuses
                ?.forEach { container ->
                    this.pushContainerStatus(container)
                }
        }
    }

    override fun mapContainerStatus(containerStatus: V1ContainerStatus): ResourceStatusInfo {
        return ResourceStatusInfo.build {
            this.pushContainerStatus(containerStatus)
        }
    }

    fun ResourceStatusInfo.Builder.pushContainerStatus(containerStatus: V1ContainerStatus) {
        val waiting = containerStatus.state?.waiting
        val terminated = containerStatus.state?.terminated

        when {
            waiting != null && (waiting.reason == "ImagePullBackOff" || waiting.reason == "ErrImagePull") -> {
                error(
                    clarity = ResourceStatusMessageClarity.HIGHEST,
                    message = messagesService.containerHasFailedToDownloadImage(containerStatus.name, containerStatus.image)
                )
            }

            waiting != null && waiting.reason == "CrashLoopBackOff" -> {
                val lastExitCode = containerStatus.lastState?.terminated?.exitCode
                if (lastExitCode != null) {
                    error(
                        clarity = ResourceStatusMessageClarity.HIGH,
                        message = messagesService.containerHasFailedToStartWithExitCode(containerStatus.name, lastExitCode)
                    )
                } else {
                    error(
                        clarity = ResourceStatusMessageClarity.HIGH,
                        message = messagesService.containerHasFailedToStart(containerStatus.name)
                    )
                }
            }

            waiting != null -> error(messagesService.containerIsNotReady(containerStatus.name, waiting.message ?: "unknown"))

            terminated != null -> {
                if (terminated.message != null) {
                    error(
                        clarity = ResourceStatusMessageClarity.HIGH,
                        message = messagesService.containerHasTerminatedInFailure(containerStatus.name, terminated.message ?: "")
                    )
                } else {
                    error(
                        clarity = ResourceStatusMessageClarity.HIGH,
                        message = messagesService.containerHasTerminatedInFailureWithExitCode(containerStatus.name, terminated.exitCode)
                    )
                }
            }

            !containerStatus.ready -> {
                error(messagesService.containerIsInUnreadyState(containerStatus.name))
            }

            containerStatus.ready -> { }

            else -> error(messagesService.containerIsInUnknownState(containerStatus.name))
        }
    }

    private fun V1Pod.getCondition(type: String): V1PodCondition? {
        return this.status?.conditions?.firstOrNull {
            it.type == type
        }
    }
}