package ru.qiwi.devops.mission.control.service.security

import io.kubernetes.client.util.KubeConfig
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.config.ClustersConfig
import ru.qiwi.devops.mission.control.model.KubeCluster
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths

@Component
@ConditionalOnProperty("mission-control.tokens-source.type", havingValue = "kubeconfig")
class KubernetesConfigTokenSource(
    config: ClustersConfig
) : KubernetesTokenSource {
    private val clusters = parse(config)

    override fun getToken(name: String): String? {
        return clusters[name]?.token
    }

    private fun parse(clustersConfig: ClustersConfig): Map<String, KubeCluster> {
        val config = clustersConfig.tokensSource?.kubeconfig
            ?: throw IllegalStateException("mission-control.tokens-source.kubeconfig is required")

        val reader = getPath(config.path).toFile().reader(Charset.defaultCharset())
        val kubeconfig = KubeConfig.loadKubeConfig(reader)

        val aliases = config.aliases.entries.groupBy({ it.value }, { it.key })

        val clusters = kubeconfig.clusters.asMap()
            .mapNotNull { c -> c.getString("name") safeTo c.getIn<String>("cluster", "server") }
            .toMap()

        val tokens = kubeconfig.users.asMap()
            .mapNotNull { u -> u.getString("name") safeTo (u.getIn<String>("user", "auth-provider", "config", "id-token") ?: "") }
            .toMap()

        return kubeconfig.contexts.asMap()
            .flatMap { c ->
                val name = c.getString("name")
                    ?: throw IllegalStateException("Can't get cluster name from kubeconfig")
                val token = c.getIn<String>("context", "user")?.let { tokens[it] }
                    ?: throw IllegalStateException("Can't find token for cluster $name from kubeconfig")
                val host = c.getIn<String>("context", "cluster")?.let { clusters[it] }
                    ?: throw IllegalStateException("Can't find host for cluster $name from kubeconfig")

                aliases[name]?.map { alias ->
                    createPair(alias, name, host, token)
                } ?: listOf(createPair(name, name, host, token))
            }.toMap()
    }

    private fun createPair(name: String, displayName: String, host: String, token: String): Pair<String, KubeCluster> {
        return name to KubeCluster(
            name = name,
            displayName = displayName,
            host = host,
            dataCenter = "",
            token = token
        )
    }

    private fun getPath(path: String): Path {
        return if (path.startsWith("~" + File.separator)) {
            Paths.get(System.getProperty("user.home"), path.substring(1))
        } else {
            Paths.get(path)
        }
    }

    private fun Iterable<Any>.asMap(): Iterable<Map<String, Any>> {
        return this.map { c -> c as Map<String, Any> }
    }

    private fun Map<String, Any>.getString(key: String): String? {
        return this[key] as? String
    }

    private infix fun <T1, T2> T1?.safeTo(second: T2?): Pair<T1, T2>? {
        return this?.let { f -> second?.let { s -> f to s } }
    }

    private fun <T> Map<String, Any>.getIn(vararg key: String): T? {
        val value = this[key[0]]
        if (value == null || key.size == 1) {
            return value as T
        }

        return (value as? Map<String, Any>)?.let { it.getIn(*key.drop(1).toTypedArray()) }
    }
}