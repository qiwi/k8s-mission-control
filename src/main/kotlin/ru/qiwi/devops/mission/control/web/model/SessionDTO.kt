package ru.qiwi.devops.mission.control.web.model

import com.fasterxml.jackson.annotation.JsonInclude
import ru.qiwi.devops.mission.control.web.security.WebToken

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SessionDTO(
    val userName: String,
    val displayName: String,
    val roles: List<String>,
    val csrfToken: String?,
    val token: String?
)

fun WebToken.toSessionDTO(token: String? = null) = SessionDTO(this.userName, this.displayName, this.roles, this.csrfToken, token)