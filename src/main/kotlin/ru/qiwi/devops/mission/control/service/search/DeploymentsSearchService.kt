package ru.qiwi.devops.mission.control.service.search

import org.springframework.stereotype.Service
import ru.qiwi.devops.mission.control.model.DeploymentInfo
import ru.qiwi.devops.mission.control.service.deployment.DeploymentCache
import ru.qiwi.devops.mission.control.utils.getLogger

@Service
class DeploymentsSearchService(
    private val deploymentCache: DeploymentCache
) : SearchService {
    private val logger = getLogger<DeploymentsSearchService>()

    override fun search(request: SearchRequest): SearchResult {
        logger.info("Looking for deployments by request $request")

        val words = request.filter
            .split(" ")
            .filter { it.isNotEmpty() }

        val items = deploymentCache.getAll()
            .mapNotNull { deployment ->
                val score = words
                    .map { word ->
                        when {
                            deployment.metadata.name.contains(word) -> {
                                word.length * 100 / deployment.metadata.name.length
                            }
                            deployment.metadata.clusterName.contains(word) -> {
                                word.length * 100 / deployment.metadata.clusterName.length
                            }
                            else -> 0
                        }
                    }.computeTotalScore()

                if (score > 0) {
                    DeploymentSearchResultItem(deployment, score)
                } else {
                    null
                }
            }
            .sorted()

        val total = items.size
        val limitedItems = items.take(request.limit)
        return SearchResult(limitedItems, total)
    }

    private fun Iterable<Int>.computeTotalScore(): Int {
        return this.fold(0, { acc, n -> if (acc == -1 || n == 0) -1 else acc + n })
    }

    class DeploymentSearchResultItem(deployment: DeploymentInfo, override val score: Int) : SearchResultItem<DeploymentKey> {
        override val type: String = "deployment"
        override val title: String = deployment.metadata.name
        override val value: DeploymentKey = DeploymentKey(deployment.metadata.name, deployment.metadata.namespace, deployment.metadata.clusterName)
    }

    data class DeploymentKey(
        val name: String,
        val namespace: String,
        val clusterName: String
    )
}
