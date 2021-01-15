package ru.qiwi.devops.mission.control.service.k8s

import io.kubernetes.client.informer.ResourceEventHandler
import io.kubernetes.client.informer.SharedIndexInformer
import io.kubernetes.client.informer.cache.Indexer
import io.kubernetes.client.informer.impl.DefaultSharedIndexInformer
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.util.CallGeneratorParams
import okhttp3.Call
import ru.qiwi.devops.mission.control.service.health.cluster.ClusterHealthReceiver
import ru.qiwi.devops.mission.control.utils.getLogger
import java.io.Closeable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.function.Function

class CustomInformerFactory(
    private val clusterName: String,
    private val apiClient: ApiClient,
    private val executor: ExecutorService = Executors.newCachedThreadPool(),
    private val healthReceiver: ClusterHealthReceiver
) : Closeable {
    companion object {
        private val logger = getLogger<CustomInformerFactory>()
    }

    private val lock = Any()
    private val informers = mutableListOf<ActiveInformer<*>>()
    private val startedInformers = mutableMapOf<Int, Future<*>>()

    init {
        if (this.apiClient.httpClient.readTimeoutMillis > 0) {
            throw IllegalStateException("Can't use apiClient with non-zero readTimeout")
        }
    }

    fun <T, L> create(
        callGenerator: (CallGeneratorParams) -> Call,
        apiTypeClass: Class<T>,
        apiListTypeClass: Class<L>,
        resyncPeriodInMillis: Long = 0
    ): SharedIndexInformer<T> {
        synchronized(lock) {
            val listerWatcher = InspectableListerWatcher(
                clusterName, apiClient, callGenerator, apiTypeClass, apiListTypeClass, healthReceiver
            )
            val baseInformer = DefaultSharedIndexInformer<T, L>(apiTypeClass, listerWatcher, resyncPeriodInMillis)
            val id = informers.size
            val informer = ActiveInformer(
                informer = baseInformer,
                onStart = { startInformer(id) },
                onStop = { stopInformer(id) }
            )

            informers.add(informer)

            return informer
        }
    }

    override fun close() {
        synchronized(lock) {
            logger.info("Stopping all informers for cluster $clusterName...")
            this.informers.forEachIndexed { id, informer ->
                if (this.startedInformers.remove(id) != null) {
                    informer.stopInternal()
                }
            }
            if (this.executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                logger.error("Some informers for cluster $clusterName haven't stopped gracefully, force shutdown")
            }
        }
    }

    private fun startInformer(id: Int) {
        synchronized(lock) {
            startedInformers.computeIfAbsent(id) {
                executor.submit {
                    informers[id].startInternal()
                }
            }
        }
    }

    private fun stopInformer(id: Int) {
        synchronized(lock) {
            val future = startedInformers.remove(id)
            if (future != null) {
                informers[id].stopInternal()
            }
        }
    }

    private class ActiveInformer<T>(
        private val informer: SharedIndexInformer<T>,
        private val onStart: () -> Unit,
        private val onStop: () -> Unit
    ) : SharedIndexInformer<T> {
        fun startInternal() = informer.run()

        fun stopInternal() = informer.stop()

        override fun run() {
            onStart()
        }

        override fun stop() {
            onStop()
        }

        override fun addEventHandler(handler: ResourceEventHandler<T>) {
            informer.addEventHandler(handler)
        }

        override fun addEventHandlerWithResyncPeriod(handler: ResourceEventHandler<T>, resyncPeriodMillis: Long) {
            informer.addEventHandlerWithResyncPeriod(handler, resyncPeriodMillis)
        }

        override fun addIndexers(indexers: MutableMap<String, Function<T, MutableList<String>>>) {
            informer.addIndexers(indexers)
        }

        override fun getIndexer(): Indexer<T> = informer.indexer

        override fun hasSynced(): Boolean = informer.hasSynced()

        override fun lastSyncResourceVersion(): String = informer.lastSyncResourceVersion()
    }
}

inline fun <reified T, reified TList> CustomInformerFactory.create(
    noinline callGenerator: (CallGeneratorParams) -> Call,
    resyncPeriodInMillis: Long = 0
): SharedIndexInformer<T> {
    return this.create(callGenerator, T::class.java, TList::class.java, resyncPeriodInMillis)
}