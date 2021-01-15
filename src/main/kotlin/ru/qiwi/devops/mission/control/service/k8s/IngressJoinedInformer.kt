package ru.qiwi.devops.mission.control.service.k8s

import io.kubernetes.client.informer.SharedInformer
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.qiwi.devops.mission.control.service.cluster.ClusterService
import ru.qiwi.devops.mission.control.service.namespace.NamespaceServiceFactory

@Service
class IngressJoinedInformer(
    clusterService: ClusterService,
    namespaceServiceFactory: NamespaceServiceFactory
) : AbstractJoinedInformer<ExtensionsV1beta1Ingress>(clusterService, namespaceServiceFactory, ExtensionsV1beta1Ingress::class.java) {
    override fun createInformer(namespace: String, client: KubernetesClient): SharedInformer<ExtensionsV1beta1Ingress> {
        return client.createIngressInformer(namespace)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun start() {
        this.startInformers()
    }
}