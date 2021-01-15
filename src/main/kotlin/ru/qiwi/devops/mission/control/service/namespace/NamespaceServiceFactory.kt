package ru.qiwi.devops.mission.control.service.namespace

interface NamespaceServiceFactory {
    fun createNamespaceService(clusterName: String): NamespaceService?
}