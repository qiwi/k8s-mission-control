package ru.qiwi.devops.mission.control.service.k8s

import io.kubernetes.client.informer.SharedInformer
import ru.qiwi.devops.mission.control.service.cluster.ClusterService
import ru.qiwi.devops.mission.control.service.namespace.NamespaceServiceFactory
import ru.qiwi.devops.mission.control.utils.getLogger

abstract class AbstractJoinedInformer<T>(
    private val clusterService: ClusterService,
    private val namespaceServiceFactory: NamespaceServiceFactory,
    private val resourceClass: Class<T>
) : Informer<T> {
    private val logger = getLogger<AbstractJoinedInformer<*>>()

    private val informers = createAllInformers()

    abstract fun createInformer(namespace: String, client: KubernetesClient): SharedInformer<T>

    override fun addEventCallback(callback: InformerEventCallback<T>) {
        informers.forEach {
            it.informer.addEventHandler(createEventHandler(callback, it.clusterName))
        }
    }

    override fun addCloseCallback(callback: () -> Unit) { }

    private fun createAllInformers(): List<InformerInfo<T>> {
        logger.info("Creating informers for ${resourceClass.simpleName}...")
        return clusterService.getClusters()
            .flatMap { cluster ->
                val client = clusterService.getClusterClient(cluster.name)
                    ?: throw IllegalStateException("Can't find client fot cluster ${cluster.name}")

                try {
                    getNamespaces(client)
                        .map { ns -> createInformer(ns, client) }
                        .map { informer -> InformerInfo(cluster.name, informer) }
                } catch (e: Throwable) {
                    logger.warn("Can't create informer for ${resourceClass.simpleName} for cluster ${client.clusterName}", e)
                    emptyList<InformerInfo<T>>()
                }
            }
    }

    private fun getNamespaces(client: KubernetesClient): List<String> {
        val nsService = namespaceServiceFactory.createNamespaceService(client.clusterName)
            ?: throw java.lang.IllegalStateException("Can't create namespace service for cluster ${client.clusterName}")
        return nsService.getAccessibleNamespaces().map { it.name }
    }

    protected fun startInformers() {
        logger.info("Starting all informers for ${resourceClass.simpleName}...")
        informers.forEach {
            it.informer.run()
        }
    }

    private data class InformerInfo<T>(
        val clusterName: String,
        val informer: SharedInformer<T>
    )
}