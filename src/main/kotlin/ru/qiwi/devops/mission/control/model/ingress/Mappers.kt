package ru.qiwi.devops.mission.control.model.ingress

import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress
import ru.qiwi.devops.mission.control.model.ResourceStatusInfo
import ru.qiwi.devops.mission.control.service.toMetadataInfo

fun ExtensionsV1beta1Ingress.toIngressInfo(clusterName: String): IngressInfo {
    return IngressInfo(
        metadata = this.metadata.toMetadataInfo(clusterName),
        status = ResourceStatusInfo.OK,
        rules = this.spec?.rules?.map { rule ->
            IngressRuleInfo(host = rule.host ?: "", http = rule.http?.paths?.map { path ->
                IngressHttpPath(path = path.path ?: "", backend = path.backend.let { backend ->
                    IngressHttpBackend(serviceName = backend.serviceName, servicePort = backend.servicePort.toString())
                })
            } ?: emptyList())
        } ?: emptyList()
    )
}