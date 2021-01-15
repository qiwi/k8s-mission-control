package ru.qiwi.devops.mission.control.web.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.qiwi.devops.mission.control.service.endpoint.EndpointService
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.web.model.EndpointGroupDTO

@RestController
@RequestMapping("/api/clusters/{clusterName}")
class EndpointsController(
    private val endpointService: EndpointService
) {
    private val logger = getLogger<EndpointsController>()

    @GetMapping("/namespaces/{namespace}/deployments/{deploymentName}/endpoints")
    fun getEndpointsByDeployment(
        @PathVariable clusterName: String,
        @PathVariable namespace: String,
        @PathVariable deploymentName: String
    ): ResponseEntity<List<EndpointGroupDTO>> {
        logger.info("Looking for endpoints associated with $namespace/$deploymentName in $clusterName")
        return endpointService.getEndpoints(namespace, deploymentName)
            .groupBy { it.target }
            .map { EndpointGroupDTO(it.key, it.value) }
            .let { ResponseEntity.ok(it) }
    }
}