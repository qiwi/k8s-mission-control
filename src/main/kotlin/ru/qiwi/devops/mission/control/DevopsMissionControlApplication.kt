package ru.qiwi.devops.mission.control

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("ru.qiwi.devops.mission.control.config")
class DevopsMissionControlApplication

fun main(args: Array<String>) {
    runApplication<DevopsMissionControlApplication>(*args) {
        setLogStartupInfo(true)
        setBannerMode(Banner.Mode.OFF)
    }
}
