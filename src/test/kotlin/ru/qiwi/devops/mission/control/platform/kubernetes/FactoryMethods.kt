package ru.qiwi.devops.mission.control.platform.kubernetes

import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.proto.V1beta1Extensions
import io.kubernetes.client.util.Yaml
import ru.qiwi.devops.mission.control.platform.configuration.TestConfig
import java.util.UUID

fun createDeploymentFromResources(name: String): V1Deployment {
    return readResource<V1Deployment>("deployments/$name")
        .apply { metadata = generateMetadata() }
}

fun createDeployment(namespace: String, name: String): V1Deployment {
    return V1Deployment().apply {
        metadata = V1ObjectMeta().apply {
            this.name = name
            this.namespace = namespace
        }
    }
}

fun createIngress(namespace: String, name: String): V1beta1Extensions.Ingress {
    return V1beta1Extensions.Ingress.newBuilder()
        .apply {
            this.metadataBuilder.apply {
                this.namespace = namespace
                this.name = name
            }
        }
        .build()
}

private inline fun <reified T> readResource(name: String): T {
    val path = "fixtures/$name.yaml"
    val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(path)
        ?: throw IllegalStateException("Can not find resource $path")
    return Yaml.load(stream.reader(Charsets.UTF_8)) as T
}

private fun generateMetadata(): V1ObjectMeta {
    return V1ObjectMeta().apply {
        namespace = TestConfig.kubernetes.generating.namespace
        name = TestConfig.kubernetes.generating.namePrefix + UUID.randomUUID()
        putAnnotationsItem("created-for-tests", TestConfig.applicationName)
    }
}