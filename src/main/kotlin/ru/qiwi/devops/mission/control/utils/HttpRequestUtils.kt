package ru.qiwi.devops.mission.control.utils

import org.springframework.web.servlet.HandlerMapping
import javax.servlet.http.HttpServletRequest

fun HttpServletRequest.isApiRequest(): Boolean {
    return this.requestURI.startsWith("/api")
}

fun HttpServletRequest.findClusterName(): String? {
    return this.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)
        ?.let { it as HashMap<String, String> }
        ?.let { it["clusterName"] }
}

fun HttpServletRequest.requireClusterName(): String {
    return this.findClusterName()
        ?: throw IllegalStateException("Can't fetch cluster name from the request")
}