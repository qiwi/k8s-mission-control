package ru.qiwi.devops.mission.control.web.controller

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.qiwi.devops.mission.control.service.pod.PodService
import ru.qiwi.devops.mission.control.service.replicaset.ReplicaSetService
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.web.errors.ApiErrors
import ru.qiwi.devops.mission.control.web.model.ApiResponseEntity
import ru.qiwi.devops.mission.control.web.model.ReplicaSetDTO
import ru.qiwi.devops.mission.control.web.model.toDTO
import ru.qiwi.devops.mission.control.web.model.toResponseEntity

@RestController
@RequestMapping("/api/clusters/{clusterName}")
class ReplicaSetsController(
    private val replicaSetService: ReplicaSetService,
    private val podService: PodService
) {
    private val logger = getLogger<DeploymentsController>()

    @GetMapping("/namespaces/{namespace}/deployments/{deploymentName}/replicasets")
    fun getReplicaSetsByDeployments(
        @PathVariable clusterName: String,
        @PathVariable namespace: String,
        @PathVariable deploymentName: String
    ): ResponseEntity<List<ReplicaSetDTO>> {
        logger.info("Looking for replicasets owned by $namespace/$deploymentName in $clusterName")

        return replicaSetService.getReplicaSetsByDeployment(namespace, deploymentName)
            .map { it.toDTO(LocaleContextHolder.getLocale()) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/namespaces/{namespace}/replicasets/{replicaset}")
    fun findPod(
        @PathVariable clusterName: String,
        @PathVariable namespace: String,
        @PathVariable replicaset: String
    ): ApiResponseEntity<ReplicaSetDTO> {
        logger.info("Looking for replicasets $namespace/$replicaset in $clusterName")

        return replicaSetService.findReplicaSet(namespace, replicaset)
            ?.toDTO(LocaleContextHolder.getLocale())
            ?.enrichWithPods()
            ?.toResponseEntity()
            ?: ApiErrors.RESOURCE_NOT_FOUND.toResponseEntity()
    }

    fun ReplicaSetDTO.enrichWithPods(): ReplicaSetDTO {
        return this.copy(
            info = this.info.copy(
                pods = podService.getPodsByReplicaSet(this.metadata.namespace, this.metadata.name)
                    .map { it.toDTO(LocaleContextHolder.getLocale()) }
            )
        )
    }
}