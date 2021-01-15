package ru.qiwi.devops.mission.control.web.controller

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.qiwi.devops.mission.control.service.deployment.DeploymentCache
import ru.qiwi.devops.mission.control.service.deployment.DeploymentService
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.web.errors.ApiErrors
import ru.qiwi.devops.mission.control.web.model.ApiResponseEntity
import ru.qiwi.devops.mission.control.web.model.DeploymentDTO
import ru.qiwi.devops.mission.control.web.model.toDTO
import ru.qiwi.devops.mission.control.web.model.toResponseEntity

@RestController
@RequestMapping("/api/clusters/{clusterName}")
class DeploymentsController(
    private val deploymentService: DeploymentService,
    private val deploymentCache: DeploymentCache
) {
    private val logger = getLogger<DeploymentsController>()

    @GetMapping("/deployments")
    fun getDeployments(
        @PathVariable clusterName: String,
        @RequestParam deploymentCondition: String?
    ): ApiResponseEntity<List<DeploymentDTO>> {
        logger.info("Looking for deployments in $clusterName")
        return deploymentCache.getByCluster(clusterName)
            .sortedBy { it.metadata.namespace + it.metadata.name }
            .map { it.toDTO(LocaleContextHolder.getLocale()) }
            .let { it.toResponseEntity() }
    }

    @GetMapping("/namespaces/{namespace}/deployments")
    fun getDeploymentsInNamespace(
        @PathVariable clusterName: String,
        @PathVariable namespace: String
    ): ApiResponseEntity<List<DeploymentDTO>> {
        logger.info("Looking for deployments in namespace $namespace and cluster $clusterName ")
        return deploymentCache.getNamespace(clusterName, namespace)
            .map { it.toDTO(LocaleContextHolder.getLocale()) }
            .let { it.toResponseEntity() }
    }

    @GetMapping("/namespaces/{namespace}/deployments/{deploymentName}")
    fun findDeployment(
        @PathVariable clusterName: String,
        @PathVariable namespace: String,
        @PathVariable deploymentName: String
    ): ApiResponseEntity<DeploymentDTO> {
        logger.info("Looking for deployment $namespace/$deploymentName in $clusterName")

        return deploymentService.findDeployment(namespace, deploymentName)
            ?.toDTO(LocaleContextHolder.getLocale())
            ?.toResponseEntity()
            ?: ApiErrors.RESOURCE_NOT_FOUND.toResponseEntity()
    }
}