package ru.qiwi.devops.mission.control.web.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorDTO(
    val errorCode: String,
    val description: String? = null,
    val params: Map<String, String>? = null
)