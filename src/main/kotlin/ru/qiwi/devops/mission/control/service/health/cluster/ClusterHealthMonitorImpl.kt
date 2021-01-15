package ru.qiwi.devops.mission.control.service.health.cluster

import io.github.resilience4j.core.metrics.FixedSizeSlidingWindowMetrics
import io.github.resilience4j.core.metrics.Metrics
import io.github.resilience4j.core.metrics.Snapshot
import ru.qiwi.devops.mission.control.utils.getLogger
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class ClusterHealthMonitorImpl(
    private val clusterName: String,
    private val config: ClusterHealthMonitorConfig
) : ClusterHealthMonitor, ClusterHealthReceiver {
    companion object {
        private val logger = getLogger<ClusterHealthMonitorImpl>()
    }

    private val metrics = FixedSizeSlidingWindowMetrics(config.slidingWindowSize)
    private val currentStatus = AtomicBoolean(true)

    override fun getCurrentStatus(): ClusterHealth {
        val value = if (currentStatus.get()) {
            ClusterHealthStatus.OK
        } else {
            ClusterHealthStatus.DOWN
        }
        return ClusterHealth(value)
    }

    override fun getReceiver(): ClusterHealthReceiver = this

    override fun onSuccess(duration: Duration) {
        update(metrics.record(duration.toMillis(), TimeUnit.MILLISECONDS, Metrics.Outcome.SUCCESS))
    }

    override fun onError(duration: Duration) {
        update(metrics.record(duration.toMillis(), TimeUnit.MILLISECONDS, Metrics.Outcome.ERROR))
    }

    private fun update(snapshot: Snapshot) {
        val status = snapshot.totalNumberOfCalls < config.minNumberOfCalls || snapshot.failureRate < config.failureRateThreshold
        if (currentStatus.compareAndSet(!status, status)) {
            if (status) {
                logger.info("Cluster $clusterName has been marked as healthy. There are only ${snapshot.failureRate}% of fails from the last ${snapshot.totalNumberOfCalls} times")
            } else {
                logger.error("Cluster $clusterName has been marked as unhealthy. There are as much as ${snapshot.failureRate}% of fails from the last ${snapshot.totalNumberOfCalls} times")
            }
        }
    }
}
