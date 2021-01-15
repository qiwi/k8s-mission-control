package ru.qiwi.devops.mission.control.config

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("mission-control.frontend.config")
@JsonInclude(JsonInclude.Include.NON_NULL)
class FrontendAppConfig {
    var site: SiteConfig = SiteConfig()
    var api: ApiConfig = ApiConfig()

    @JsonInclude(JsonInclude.Include.NON_NULL)
    class SiteConfig {
        var baseUrl: String? = null
        var features: Map<String, String> = emptyMap()
        var metrics: SiteMetricsConfig = SiteMetricsConfig()
    }

    class SiteMetricsConfig {
        var yandexMetrika: SiteYandexMetrikaConfig? = null
    }

    class SiteYandexMetrikaConfig {
        var enable: Boolean = false
        var account: String = ""
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    class ApiConfig {
        var url: String? = null
    }
}