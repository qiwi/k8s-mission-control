package ru.qiwi.devops.mission.control.model

data class KubeCluster(
    val name: String,
    val displayName: String,
    val host: String,
    val dataCenter: String,
    val token: String?
) {
    fun hasAuthentication(): Boolean = !token.isNullOrBlank()
}
