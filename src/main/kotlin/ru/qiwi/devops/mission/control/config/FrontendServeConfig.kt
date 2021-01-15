package ru.qiwi.devops.mission.control.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("mission-control.frontend.serve")
class FrontendServeConfig {
    var enable: Boolean = false
    var directory: String? = null
}