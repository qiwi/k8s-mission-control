package ru.qiwi.devops.mission.control.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.qiwi.devops.mission.control.web.model.ApiResponseEntity
import ru.qiwi.devops.mission.control.web.model.toResponseEntity
import ru.qiwi.devops.mission.control.model.NamespaceInfo
import ru.qiwi.devops.mission.control.service.namespace.OverallNamespaceService
import ru.qiwi.devops.mission.control.utils.getLogger

@RestController
@RequestMapping("/api/namespaces")
class NamespaceController(
    val overallNamespaceService: OverallNamespaceService
) {
    private val logger = getLogger<ClusterController>()

    @GetMapping
    fun getNamespacesInAllClusters(): ApiResponseEntity<List<NamespaceInfo>> {
        logger.info("Looking for namespaces in all clusters")

        return overallNamespaceService.getUniqueNamespacesInAllClusters().toResponseEntity()
    }
}