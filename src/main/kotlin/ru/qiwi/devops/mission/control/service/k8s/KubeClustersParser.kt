package ru.qiwi.devops.mission.control.service.k8s

import io.kubernetes.client.util.KubeConfig
import ru.qiwi.devops.mission.control.model.KubeCluster
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths

class KubeClustersParser {
    companion object {
        fun fromFile(path: String): List<KubeCluster> {
            val reader = resolvePath(path).toFile().reader(Charset.defaultCharset())
            val parsed = KubeConfig.loadKubeConfig(reader)
            return parse(parsed)
        }

        private fun parse(kubeconfig: KubeConfig): List<KubeCluster> {
            val clusters = kubeconfig.clusters.asMap { name, entry ->
                val server = requireNotNull(entry.getIn<String>("cluster", "server"))
                Cluster(name, server)
            }

            val users = kubeconfig.users.asMap { name, entry ->
                val token = entry.getIn<String>("user", "auth-provider", "config", "id-token") ?: ""
                User(name, token)
            }

            val contexts = kubeconfig.contexts.asMap { name, entry ->
                val user = requireNotNull(entry.getIn<String>("context", "user"))
                val cluster = requireNotNull(entry.getIn<String>("context", "cluster"))
                Context(name, user, cluster)
            }

            return contexts.map { (name, context) ->
                val cluster = requireNotNull(clusters[context.cluster])
                val user = requireNotNull(users[context.user])

                KubeCluster(
                    name = name,
                    displayName = name,
                    host = cluster.server,
                    dataCenter = "",
                    token = user.token
                )
            }
        }

        private fun <T> Iterable<Any>.asMap(mapper: (key: String, entry: Map<String, Any>) -> T?): Map<String, T> {
            return this.map { c -> c as Map<String, Any> }
                .mapNotNull { entry ->
                    entry.getString("name")?.let { name ->
                        mapper(name, entry)?.let { value ->
                            name to value
                        }
                    }
                }.toMap()
        }

        private fun Map<String, Any>.getString(key: String): String? {
            return this[key] as? String
        }

        private fun <T> Map<String, Any>.getIn(vararg key: String): T? {
            val value = this[key[0]]
            if (value == null || key.size == 1) {
                return value as T
            }

            return (value as? Map<String, Any>)?.let { it.getIn(*key.drop(1).toTypedArray()) }
        }

        private fun resolvePath(path: String): Path {
            return if (path.startsWith("~" + File.separator)) {
                Paths.get(System.getProperty("user.home"), path.substring(1))
            } else {
                Paths.get(path)
            }
        }
    }

    data class Cluster(
        val name: String,
        val server: String
    )

    data class User(
        val name: String,
        val token: String
    )

    data class Context(
        val name: String,
        val user: String,
        val cluster: String
    )
}