package ru.qiwi.devops.mission.control.service.factory

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.annotation.RequestScope
import ru.qiwi.devops.mission.control.service.deployment.DeploymentService
import ru.qiwi.devops.mission.control.service.endpoint.EndpointService
import ru.qiwi.devops.mission.control.service.log.LogService
import ru.qiwi.devops.mission.control.service.pod.PodService
import ru.qiwi.devops.mission.control.service.replicaset.ReplicaSetService
import ru.qiwi.devops.mission.control.service.service.ServiceService
import ru.qiwi.devops.mission.control.utils.requireClusterName
import javax.servlet.http.HttpServletRequest

@Configuration
class ClusterDependentServiceBeanCreator {
    @Bean
    @RequestScope
    fun deploymentService(factory: ClusterDependentServiceFactory, req: HttpServletRequest): DeploymentService {
        return factory.createDeploymentService(req.requireClusterName())
    }

    @Bean
    @RequestScope
    fun podService(factory: ClusterDependentServiceFactory, req: HttpServletRequest): PodService {
        return factory.createPodService(req.requireClusterName())
    }

    @Bean
    @RequestScope
    fun replicaSetService(factory: ClusterDependentServiceFactory, req: HttpServletRequest): ReplicaSetService {
        return factory.createReplicaSetService(req.requireClusterName())
    }

    @Bean
    @RequestScope
    fun serviceService(factory: ClusterDependentServiceFactory, req: HttpServletRequest): ServiceService {
        return factory.createServiceService(req.requireClusterName())
    }

    @Bean
    @RequestScope
    fun endpointService(factory: ClusterDependentServiceFactory, req: HttpServletRequest): EndpointService {
        return factory.createEndpointService(req.requireClusterName())
    }

    @Bean
    @RequestScope
    fun logService(factory: ClusterDependentServiceFactory, req: HttpServletRequest): LogService {
        return factory.createLogService(req.requireClusterName())
    }
}