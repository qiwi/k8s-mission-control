package ru.qiwi.devops.mission.control.utils

import io.kubernetes.client.openapi.models.V1Namespace
import io.kubernetes.client.openapi.models.V1ObjectMeta

fun createNamespace(name: String): V1Namespace {
    return V1Namespace().apply {
        metadata = V1ObjectMeta().apply {
            setName(name)
        }
    }
}