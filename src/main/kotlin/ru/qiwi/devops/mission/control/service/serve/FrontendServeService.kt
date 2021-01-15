package ru.qiwi.devops.mission.control.service.serve

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.config.FrontendServeConfig
import ru.qiwi.devops.mission.control.utils.Mime2FileTypeMap
import ru.qiwi.devops.mission.control.web.model.StaticFileDTO
import ru.qiwi.devops.mission.control.utils.getLogger
import java.io.File

@Component
@ConditionalOnProperty("mission-control.frontend.serve.enable", havingValue = "true")
class FrontendServeService(
    serveConfig: FrontendServeConfig
) {
    private val logger = getLogger<FrontendServeConfig>()
    private val directory = requireDirectory(serveConfig)

    fun readFile(path: String): StaticFileDTO? {
        return readFileInternal(path) ?: readFileInternal("index.html")
    }

    private fun readFileInternal(path: String): StaticFileDTO? {
        val file = File(directory, path)

        if (!file.canonicalPath.startsWith(directory)) {
            logger.error("It seems like someone tried to hack us using directory traversal...")
            return null
        }

        if (!file.exists() || !file.isFile) {
            return null
        }

        return StaticFileDTO(
            content = file.readBytes(),
            mediaType = Mime2FileTypeMap.getMediaType(file.name) ?: MediaType.TEXT_PLAIN
        )
    }

    private fun requireDirectory(serveConfig: FrontendServeConfig): String {
        val directory = serveConfig.directory
            ?: throw IllegalStateException("Property mission-control.frontend.serve.directory cannot be empty")
        return File(directory).canonicalPath
    }
}