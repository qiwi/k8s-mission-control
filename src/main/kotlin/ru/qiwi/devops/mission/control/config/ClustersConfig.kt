package ru.qiwi.devops.mission.control.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("mission-control")
class ClustersConfig(
    val tokensSource: TokensSource? = null,
    val clusters: List<ClusterRecord>,
    val tokens: List<TokenRecord> = emptyList()
)

class TokensSource(
    val type: TokensSourceType,
    val kubeconfig: KubeconfigTokensSource? = null,
    val vault: VaultTokensSource? = null
)

enum class TokensSourceType {
    NONE,
    KUBECONFIG,
    VAULT
}

class KubeconfigTokensSource(
    val path: String,
    val aliases: Map<String, String> = emptyMap()
)

class VaultTokensSource(
    val path: String,
    val keyTemplate: String
)

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
