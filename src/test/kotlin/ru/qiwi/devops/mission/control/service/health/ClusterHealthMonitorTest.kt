package ru.qiwi.devops.mission.control.service.health

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.qiwi.devops.mission.control.service.health.cluster.ClusterHealthMonitorConfig
import ru.qiwi.devops.mission.control.service.health.cluster.ClusterHealthMonitorImpl
import ru.qiwi.devops.mission.control.service.health.cluster.ClusterHealthStatus
import java.time.Duration

class ClusterHealthMonitorTest {
    private lateinit var monitor: ClusterHealthMonitorImpl

    @BeforeEach
    fun init() {
        monitor = ClusterHealthMonitorImpl(
            clusterName = "test",
            config = object : ClusterHealthMonitorConfig {
                override val minNumberOfCalls: Int = 5
                override val failureRateThreshold: Int = 50
                override val slidingWindowSize: Int = 10
            }
        )
    }

    @Test
    fun `Should report OK for empty sequence`() {
        assertThat(monitor.getCurrentStatus().value).isEqualTo(ClusterHealthStatus.OK)
    }

    @Test
    fun `Should report OK for insufficient number of data`() {
        monitor.apply(error(4))
        assertThat(monitor.getCurrentStatus().value).isEqualTo(ClusterHealthStatus.OK)
    }

    @Test
    fun `Should report DOWN for 100% of errors`() {
        monitor.apply(error(10))
        assertThat(monitor.getCurrentStatus().value).isEqualTo(ClusterHealthStatus.DOWN)
    }

    @Test
    fun `Should report DOWN for 50% of errors and 50% of success`() {
        monitor.apply(error(5), success(5))
        assertThat(monitor.getCurrentStatus().value).isEqualTo(ClusterHealthStatus.DOWN)
    }

    @Test
    fun `Should report DOWN for 50% of success and 50% of errors`() {
        monitor.apply(success(5), error(5))
        assertThat(monitor.getCurrentStatus().value).isEqualTo(ClusterHealthStatus.DOWN)
    }

    @Test
    fun `Should report DOWN for 50% of success and 50% of errors (unordered)`() {
        monitor.apply(success(3), error(5), success(2))
        assertThat(monitor.getCurrentStatus().value).isEqualTo(ClusterHealthStatus.DOWN)
    }

    @Test
    fun `Should report OK for less than 50% of errors`() {
        monitor.apply(success(6), error(4))
        assertThat(monitor.getCurrentStatus().value).isEqualTo(ClusterHealthStatus.OK)
    }

    @Test
    fun `Should check only last 10 entries`() {
        monitor.apply(error(14), success(6))
        assertThat(monitor.getCurrentStatus().value).isEqualTo(ClusterHealthStatus.OK)
    }

    private fun ClusterHealthMonitorImpl.apply(vararg sequence: List<Boolean>) {
        sequence.forEach { s ->
            s.forEach { b ->
                if (b) {
                    this.onSuccess(Duration.ZERO)
                } else {
                    this.onError(Duration.ZERO)
                }
            }
        }
    }

    private fun success(count: Int): List<Boolean> {
        return (1..count).map { true }
    }

    private fun error(count: Int): List<Boolean> {
        return (1..count).map { false }
    }
}