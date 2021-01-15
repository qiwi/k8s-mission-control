package ru.qiwi.devops.mission.control.web.model

import org.springframework.http.MediaType

data class StaticFileDTO(
    val content: ByteArray,
    val mediaType: MediaType
)