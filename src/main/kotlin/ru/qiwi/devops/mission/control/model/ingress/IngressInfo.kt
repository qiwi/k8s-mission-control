package ru.qiwi.devops.mission.control.model.ingress

import ru.qiwi.devops.mission.control.model.AbstractResource
import ru.qiwi.devops.mission.control.model.MetadataInfo
import ru.qiwi.devops.mission.control.model.ResourceStatusInfo

data class IngressInfo(
    override val metadata: MetadataInfo,
    override val status: ResourceStatusInfo,
    val rules: List<IngressRuleInfo>
) : AbstractResource