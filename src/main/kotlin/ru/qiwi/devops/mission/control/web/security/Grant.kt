package ru.qiwi.devops.mission.control.web.security

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Grant {
    READ_CLUSTERS,
    READ_DEPLOYMENTS,
    READ_LOGS,
    RESTART_DEPLOYMENT,
    SCALE_DEPLOYMENT;

    val publicName = this.name.toLowerCase()

    override fun toString() = publicName

    fun toAuthority() = SimpleGrantedAuthority(publicName)

    companion object {
        fun parse(name: String): Grant {
            return valueOf(name.toUpperCase())
        }
    }
}