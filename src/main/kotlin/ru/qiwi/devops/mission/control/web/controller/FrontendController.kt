package ru.qiwi.devops.mission.control.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.qiwi.devops.mission.control.config.FrontendAppConfig
import ru.qiwi.devops.mission.control.service.serve.FrontendServeService
import ru.qiwi.devops.mission.control.web.errors.ApiErrors
import ru.qiwi.devops.mission.control.web.model.ApiResponseEntity
import ru.qiwi.devops.mission.control.web.model.StaticFileDTO
import ru.qiwi.devops.mission.control.web.model.toResponseEntity
import javax.servlet.http.HttpServletRequest

@RestController
@ConditionalOnBean(FrontendServeService::class)
class FrontendController(
    val appConfig: FrontendAppConfig,
    val frontendServeService: FrontendServeService,
    val json: ObjectMapper
) {
    @GetMapping("/**")
    fun getIndex(request: HttpServletRequest): ResponseEntity<ByteArray> {
        return frontendServeService.readFile(request.requestURI)
            ?.let { createFileResponse(it) }
            ?: ResponseEntity.notFound().build()
    }

    // No need to serve index.html by URLs which starts with /api
    @GetMapping("/api/**")
    fun getNotFound(): ApiResponseEntity<Unit> {
        return ApiErrors.RESOURCE_NOT_FOUND.toResponseEntity()
    }

    @GetMapping("/config.js", produces = ["application/javascript; charset=UTF-8"])
    fun getJSConfig(): ResponseEntity<String> {
        val stringConfig = json.writeValueAsString(appConfig)
        return ResponseEntity.ok("window.__SERVER_CONFIG = $stringConfig")
    }

    private fun createFileResponse(file: StaticFileDTO): ResponseEntity<ByteArray> {
        val headers = HttpHeaders()
        headers.contentType = file.mediaType
        return ResponseEntity(file.content, headers, HttpStatus.OK)
    }
}