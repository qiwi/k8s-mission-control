package ru.qiwi.devops.mission.control.service.k8s

import com.google.gson.reflect.TypeToken
import io.kubernetes.client.informer.ListerWatcher
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.util.CallGeneratorParams
import io.kubernetes.client.util.Watch
import okhttp3.Call
import ru.qiwi.devops.mission.control.service.health.cluster.ClusterHealthReceiver
import ru.qiwi.devops.mission.control.utils.getLogger

class InspectableListerWatcher<T, L>(
    private val clusterName: String,
    private val apiClient: ApiClient,
    private val callGenerator: (CallGeneratorParams) -> Call,
    private val apiTypeClass: Class<T>,
    private val apiListTypeClass: Class<L>,
    private val healthReceiver: ClusterHealthReceiver
) : ListerWatcher<T, L> {
    private val logger = getLogger<InspectableListerWatcher<*, *>>()

    @Throws(ApiException::class)
    override fun list(params: CallGeneratorParams): L {
        return healthReceiver.decorate {
            try {
                val call = callGenerator(params)
                apiClient.execute<L>(call, apiListTypeClass).data
            } catch (e: Throwable) {
                logger.error("Can't list ${apiTypeClass.simpleName} in cluster $clusterName", e)
                throw e
            }
        }
    }

    @Throws(ApiException::class)
    override fun watch(params: CallGeneratorParams): Watch<T> {
        return healthReceiver.decorate {
            try {
                val call = callGenerator(params)
                val typeToken = TypeToken.getParameterized(Watch.Response::class.java, apiTypeClass)
                Watch.createWatch(apiClient, call, typeToken.type)
            } catch (e: Throwable) {
                logger.error("Can't create Watcher for ${apiTypeClass.simpleName} in cluster $clusterName", e)
                throw e
            }
        }
    }
}