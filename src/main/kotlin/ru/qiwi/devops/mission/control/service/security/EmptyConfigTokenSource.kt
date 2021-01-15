package ru.qiwi.devops.mission.control.service.security

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty("mission-control.tokens-source.type", havingValue = "none")
class EmptyConfigTokenSource() : KubernetesTokenSource {
    override fun getToken(name: String): String? {
        return null
    }
}