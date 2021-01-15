package ru.qiwi.devops.mission.control.web.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.qiwi.devops.mission.control.web.model.ApiResponseEntity
import ru.qiwi.devops.mission.control.web.model.KubeClusterDTO
import ru.qiwi.devops.mission.control.web.model.toDTO
import ru.qiwi.devops.mission.control.web.model.toResponseEntity
import ru.qiwi.devops.mission.control.service.cluster.ClusterService
import ru.qiwi.devops.mission.control.utils.getLogger

@RestController
@RequestMapping("/api/clusters")
class ClusterController(
    private val clusterService: ClusterService
) {

    private val logger = getLogger<ClusterController>()

    @GetMapping
    @PreAuthorize("hasAuthority('read_clusters')")
    fun getClusters(): ApiResponseEntity<List<KubeClusterDTO>> {
        logger.info("Looking for clusters...")

        val clusters = clusterService.getClusters()
            .map { it.toDTO() }
            .toList()

        return clusters.toResponseEntity()
    }
}
