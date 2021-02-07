package ru.qiwi.devops.mission.control.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("mission-control")
class ClustersConfig(
    val clustersSource: ClustersSource? = null,
    val clusters: List<ClusterRecord> = emptyList(),
    val tokens: List<TokenRecord> = emptyList()
)

class ClustersSource(
    val type: ClustersSourceType,
    val kubeconfig: KubeconfigClustersSource? = null
)

class KubeconfigClustersSource(
    val path: String
)

enum class ClustersSourceType {
    APPCONFIG,
    KUBECONFIG
}

class TokenRecord(
    val name: String,
    val token: String
)

class ClusterRecord(
    val name: String,
    val displayName: String,
    val host: String,
    val dc: String,
    val tokenName: String? = null
)
