package ru.qiwi.devops.mission.control.web.controller

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.qiwi.devops.mission.control.service.pod.PodService
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.web.errors.ApiErrors
import ru.qiwi.devops.mission.control.web.model.ApiResponseEntity
import ru.qiwi.devops.mission.control.web.model.PodDTO
import ru.qiwi.devops.mission.control.web.model.toDTO
import ru.qiwi.devops.mission.control.web.model.toDetailedDTO
import ru.qiwi.devops.mission.control.web.model.toResponseEntity

@RestController
@RequestMapping("/api/clusters/{clusterName}")
class PodsController(
    private val podService: PodService
) {
    private val logger = getLogger<PodsController>()

    @GetMapping("/namespaces/{namespace}/deployments/{deploymentName}/pods")
    fun getPodsByDeployments(
        @PathVariable clusterName: String,
        @PathVariable namespace: String,
        @PathVariable deploymentName: String
    ): ApiResponseEntity<List<PodDTO>> {
        logger.info("Looking for pods $namespace/$deploymentName in $clusterName")

        return podService.getPodsByDeployments(namespace, deploymentName)
            .map { it.toDTO(LocaleContextHolder.getLocale()) }
            .toResponseEntity()
    }

    @GetMapping("/namespaces/{namespace}/pods/{pod}")
    fun findPod(
        @PathVariable clusterName: String,
        @PathVariable namespace: String,
        @PathVariable pod: String
    ): ApiResponseEntity<PodDTO> {
        logger.info("Looking for pod $namespace/$pod in $clusterName")

        return podService.findPod(namespace, pod)
            ?.toDetailedDTO(LocaleContextHolder.getLocale())
            ?.toResponseEntity()
            ?: ApiErrors.RESOURCE_NOT_FOUND.toResponseEntity()
    }
}