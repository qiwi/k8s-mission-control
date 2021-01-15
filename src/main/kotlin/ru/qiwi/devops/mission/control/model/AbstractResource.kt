package ru.qiwi.devops.mission.control.model

interface AbstractResource {
    val metadata: MetadataInfo
    val status: ResourceStatusInfo
}