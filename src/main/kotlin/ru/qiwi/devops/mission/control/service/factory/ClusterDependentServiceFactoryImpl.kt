package ru.qiwi.devops.mission.control.service.factory

import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.messages.MessagesService
import ru.qiwi.devops.mission.control.service.cluster.ClusterService
import ru.qiwi.devops.mission.control.service.deployment.DeploymentMapper
import ru.qiwi.devops.mission.control.service.deployment.DeploymentService
import ru.qiwi.devops.mission.control.service.deployment.DeploymentServiceImpl
import ru.qiwi.devops.mission.control.service.endpoint.EndpointService
import ru.qiwi.devops.mission.control.service.endpoint.JoiningEndpointService
import ru.qiwi.devops.mission.control.service.ingress.IngressCache
import ru.qiwi.devops.mission.control.service.k8s.KubernetesClient
import ru.qiwi.devops.mission.control.service.k8s.NotFoundClusterKubernetesClient
import ru.qiwi.devops.mission.control.service.log.LogService
import ru.qiwi.devops.mission.control.service.log.LogServiceImpl
import ru.qiwi.devops.mission.control.service.namespace.NamespaceServiceImpl
import ru.qiwi.devops.mission.control.service.pod.PodService
import ru.qiwi.devops.mission.control.service.pod.PodServiceImpl
import ru.qiwi.devops.mission.control.service.pod.PodStatusMapper
import ru.qiwi.devops.mission.control.service.replicaset.ReplicaSetService
import ru.qiwi.devops.mission.control.service.replicaset.ReplicaSetServiceImpl
import ru.qiwi.devops.mission.control.service.service.ServiceService
import ru.qiwi.devops.mission.control.service.service.ServiceServiceImpl

@Component
class ClusterDependentServiceFactoryImpl(
    private val clusterService: ClusterService,
    // shared services
    private val messagesService: MessagesService,
    private val deploymentMapper: DeploymentMapper,
    private val podStatusMapper: PodStatusMapper,
    private val ingressCache: IngressCache
) : ClusterDependentServiceFactory {
    override fun createDeploymentService(clusterName: String): DeploymentService {
        val client = getClient(clusterName)
        val namespaceService = NamespaceServiceImpl(client)
        return DeploymentServiceImpl(client, namespaceService, deploymentMapper)
    }

    override fun createPodService(clusterName: String): PodService {
        val client = getClient(clusterName)
        val rsService = ReplicaSetServiceImpl(client, messagesService)
        return PodServiceImpl(client, rsService, podStatusMapper)
    }

    override fun createReplicaSetService(clusterName: String): ReplicaSetService {
        val client = getClient(clusterName)
        return ReplicaSetServiceImpl(client, messagesService)
    }

    override fun createServiceService(clusterName: String): ServiceService {
        val client = getClient(clusterName)
        return ServiceServiceImpl(client)
    }

    override fun createEndpointService(clusterName: String): EndpointService {
        val podService = createPodService(clusterName)
        val serviceService = createServiceService(clusterName)
        return JoiningEndpointService(podService, serviceService, ingressCache)
    }

    override fun createLogService(clusterName: String): LogService {
        val client = getClient(clusterName)
        return LogServiceImpl(client)
    }

    // It's called from bean factory,
    // so any exceptions thrown here will be mapped to BeanCreationException.
    // It's not appropriate behavior because BeanCreationException
    // will be mapped to 500 Internal Server Error in controllers.
    private fun getClient(clusterName: String): KubernetesClient {
        return clusterService.getClusterClient(clusterName)
            ?: NotFoundClusterKubernetesClient(clusterName)
    }
}