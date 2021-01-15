package ru.qiwi.devops.mission.control.service.k8s

import io.kubernetes.client.informer.SharedInformer
import io.kubernetes.client.openapi.models.V1Deployment
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.qiwi.devops.mission.control.service.cluster.ClusterService
import ru.qiwi.devops.mission.control.service.namespace.NamespaceServiceFactory

@Service
class DeploymentJoinedInformer(
    clusterService: ClusterService,
    namespaceServiceFactory: NamespaceServiceFactory
) : AbstractJoinedInformer<V1Deployment>(clusterService, namespaceServiceFactory, V1Deployment::class.java) {
    override fun createInformer(namespace: String, client: KubernetesClient): SharedInformer<V1Deployment> {
        return client.createDeploymentInformer(namespace)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun start() {
        this.startInformers()
    }
}