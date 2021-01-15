package ru.qiwi.devops.mission.control.service.namespace

import ru.qiwi.devops.mission.control.model.NamespaceInfo

interface OverallNamespaceService {

    fun getUniqueNamespacesInAllClusters(): List<NamespaceInfo>
}
