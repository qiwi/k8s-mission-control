package ru.qiwi.devops.mission.control.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import ru.qiwi.devops.mission.control.model.LogEntity
import ru.qiwi.devops.mission.control.service.log.LogService
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.web.errors.ApiErrors
import ru.qiwi.devops.mission.control.web.mapper.toLogEntityDTO
import ru.qiwi.devops.mission.control.web.model.ApiResponseEntity
import ru.qiwi.devops.mission.control.web.model.logs.LogEntityDTO
import ru.qiwi.devops.mission.control.web.model.toResponseEntity
import java.time.Instant
import java.time.format.DateTimeParseException

@RestController
@RequestMapping("/api/clusters/{clusterName}/namespaces/{namespace}/deployments/{deploymentName}")
class LogController(
    private val logService: LogService
) {
    private val logger = getLogger<PodsController>()

    @GetMapping("/logs")
    fun getLogs(
        @PathVariable(value = "clusterName") cluster: String,
        @PathVariable(value = "namespace") namespace: String,
        @PathVariable(value = "deploymentName") deployment: String,
        @RequestParam(value = "container") container: String?,
        @RequestParam(value = "pod") pod: String?,
        @RequestParam(value = "limit", required = false) limit: String,
        @RequestParam(value = "date") date: String,
        @RequestParam(value = "isBefore") isBefore: String
    ): ApiResponseEntity<LogEntityDTO> {
        logger.info("Getting logs for $cluster | $namespace | $deployment")
        return try {
            logService.getDeploymentsLog(namespace, cluster, pod.orEmpty(), container.orEmpty(),
                Instant.parse(date), isBefore.toBoolean(), limit.toInt())
                .toLogEntityDTO().toResponseEntity()
        } catch (e: IllegalArgumentException) {
            ApiErrors.BAD_REQUEST.toResponseEntity()
        } catch (e: DateTimeParseException) {
            logger.error("Error while parsing 'date' ", e)
            ApiErrors.BAD_REQUEST.toResponseEntity()
        } catch (e: Exception) {
            logger.error("Error while getting logs.", e)
            ApiErrors.INTERNAL_ERROR.toResponseEntity()
        }
    }

    @GetMapping("log/stream")
    fun getLogStream(
        @PathVariable(value = "clusterName") cluster: String,
        @PathVariable(value = "namespace") namespace: String,
        @PathVariable(value = "deploymentName") deployment: String,
        @RequestParam(value = "container") container: String,
        @RequestParam(value = "pod") pod: String,
        @RequestParam(value = "date") date: String,
        @RequestParam(value = "duration", required = false) duration: String
    ): Flux<LogEntity> {
        logger.info("Getting logs stream for $cluster | $namespace | $deployment")
        val instantDate: Instant
        try {
            instantDate = Instant.parse(date)
        } catch (e: DateTimeParseException) {
            logger.error("Error while parsing 'date' ", e)
            return Flux.empty<LogEntity>()
        }
        return logService.getStreamedDeploymentsLog(namespace, pod, container, instantDate)
    }
}