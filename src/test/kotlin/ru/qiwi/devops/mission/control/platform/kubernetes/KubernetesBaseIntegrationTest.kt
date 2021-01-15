package ru.qiwi.devops.mission.control.platform.kubernetes

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeAll
import ru.qiwi.devops.mission.control.platform.configuration.TestConfig

open class KubernetesBaseIntegrationTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun check() {
            Assumptions.assumeTrue(TestConfig.kubernetes.defaultCluster != null, "Kubernetes tests are disabled")
        }

        @AfterAll
        @JvmStatic
        fun clear() {
            Kubernetes.deleteAllCreatedResources()
        }
    }
}