package ru.qiwi.devops.mission.control.service

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.StepVerifier
import ru.qiwi.devops.mission.control.service.factory.ClusterDependentServiceFactoryImpl
import java.time.Instant

@RunWith(SpringRunner::class)
@SpringBootTest
class LogServiceImplTest {

    private val cluster = "testing-dl"
    private val namespace = "payin"
    private val pod = "payin-core-d9f7c9bbf-r2gpr"
    private val container = "payin-core"

    @Autowired
    lateinit var clusterFactory: ClusterDependentServiceFactoryImpl

    @Test
    @Ignore
    fun getLimitedLog() {
        val service = clusterFactory.createLogService(cluster)
        val log = service.getDeploymentsLog(namespace, cluster, pod, container,
            getDate(),
            true,
            100
        )
        assert(log.count() > 0)
    }

    @Test
    @Ignore
    fun getLogFromStream() {
        val service = clusterFactory.createLogService(cluster)
        val log = service.getStreamedDeploymentsLog(namespace, pod, container, getDate())
        StepVerifier.create(log)
            .consumeNextWith { println(it) }
            .consumeNextWith { println(it) }
            .consumeNextWith { println(it) }
            .thenCancel()
            .verify()
        assert(true)
    }

    private fun getDate() = Instant.ofEpochSecond(Instant.now().epochSecond - 300)
}