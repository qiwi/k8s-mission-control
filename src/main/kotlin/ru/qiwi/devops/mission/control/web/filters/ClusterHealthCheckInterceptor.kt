package ru.qiwi.devops.mission.control.web.filters

import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.WebUtils
import ru.qiwi.devops.mission.control.service.health.cluster.ClusterHealthMonitorSource
import ru.qiwi.devops.mission.control.service.health.cluster.ClusterHealthStatus
import ru.qiwi.devops.mission.control.utils.findClusterName
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.web.errors.ApiErrors
import ru.qiwi.devops.mission.control.web.errors.ApiException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ClusterHealthCheckInterceptor(
    private val monitorSource: ClusterHealthMonitorSource
) : HandlerInterceptor {
    companion object {
        private val logger = getLogger<ClusterHealthCheckInterceptor>()
    }

    override fun preHandle(req: HttpServletRequest, res: HttpServletResponse, handler: Any): Boolean {
        val clusterName = req.findClusterName() ?: return true

        val status = monitorSource.getMonitor(clusterName).getCurrentStatus()
        if (status.value == ClusterHealthStatus.OK) {
            return true
        }

        logger.warn("Cluster $clusterName is unhealthy")

        req.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ApiException(ApiErrors.CLUSTER_UNAVAILABLE, "Cluster is unhealthy"))
        req.getRequestDispatcher("/error").forward(req, res)
        return false
    }
}