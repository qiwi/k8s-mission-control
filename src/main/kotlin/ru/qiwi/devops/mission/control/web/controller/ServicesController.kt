package ru.qiwi.devops.mission.control.web.controller

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.qiwi.devops.mission.control.web.model.ServiceDTO
import ru.qiwi.devops.mission.control.web.model.toDTO
import ru.qiwi.devops.mission.control.service.service.ServiceService
import ru.qiwi.devops.mission.control.utils.getLogger

@RestController
@RequestMapping("/api/clusters/{clusterName}")
class ServicesController(
    private val serviceService: ServiceService
) {
    private val logger = getLogger<ServicesController>()

    @GetMapping("/namespaces/{namespace}/deployments/{deploymentName}/services")
    fun getReplicaSetsByDeployments(
        @PathVariable clusterName: String,
        @PathVariable namespace: String,
        @PathVariable deploymentName: String
    ): ResponseEntity<List<ServiceDTO>> {
        logger.info("Looking for services associated with $namespace/$deploymentName in $clusterName")
        return serviceService.getServicesByDeployment(namespace, deploymentName)
            .map { it.toDTO(LocaleContextHolder.getLocale()) }
            .let { ResponseEntity.ok(it) }
    }
}