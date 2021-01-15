package ru.qiwi.devops.mission.control.service.security

interface KubernetesTokenSource {
    fun getToken(name: String): String?
}