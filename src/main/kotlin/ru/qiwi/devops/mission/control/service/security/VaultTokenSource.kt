package ru.qiwi.devops.mission.control.service.security

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.vault.core.VaultTemplate
import ru.qiwi.devops.mission.control.config.ClustersConfig
import ru.qiwi.devops.mission.control.utils.getLogger

@Component
@ConditionalOnProperty("mission-control.tokens-source.type", havingValue = "vault")
class VaultTokenSource(
    private val vault: VaultTemplate,
    config: ClustersConfig
) : KubernetesTokenSource {
    private val logger = getLogger<VaultTokenSource>()

    private val tokens = loadVaultData(config)

    private fun loadVaultData(config: ClustersConfig): Map<String, String> {
        val vaultConfig = config.tokensSource?.vault ?: throw IllegalStateException("mission-control.tokens-source.vault is required")
        val path = vaultConfig.path
        val keyTemplate = vaultConfig.keyTemplate.toRegex()

        logger.info("Reading kubernetes tokens from vault...")
        val vaultResponse = vault.read(path) ?: throw IllegalStateException("Cannot read tokens from vault")

        val tokens = vaultResponse.requiredData.mapNotNull { kvp ->
            keyTemplate.matchEntire(kvp.key)?.groups?.get(1)?.let { name ->
                name.value to (kvp.value as String)
            }
        }.toMap()

        logger.info("Found tokens for ${tokens.size} clusters: ${tokens.keys}")

        return tokens
    }

    override fun getToken(name: String): String? {
        return tokens[name]
    }
}