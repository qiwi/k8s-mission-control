package ru.qiwi.devops.mission.control.service.namespace

import ru.qiwi.devops.mission.control.model.NamespaceInfo

interface NamespaceService {
    fun getAccessibleNamespaces(): List<NamespaceInfo>
}