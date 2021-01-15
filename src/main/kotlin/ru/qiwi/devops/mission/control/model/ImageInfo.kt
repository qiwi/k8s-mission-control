package ru.qiwi.devops.mission.control.model

data class ImageInfo(
    val fullName: String,
    val repositoryName: String,
    val localName: String,
    val version: String
)