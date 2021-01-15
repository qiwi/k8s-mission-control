package ru.qiwi.devops.mission.control.web.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import java.util.LinkedList

@Component
class RolesService(
    config: RolesServiceConfig
) {
    private val rolesByName = config.roles.associateBy { it.name }

    private val grantsByRole = config.roles.associate { it.name to getAllGrants(it) }

    fun mapRolesToAuthorities(roles: List<String>): List<GrantedAuthority> {
        return roles.flatMap { role ->
            grantsByRole[role] ?: emptyList()
        }.distinct().map { it.toAuthority() }
    }

    private fun getAllGrants(source: RoleDefinition): Iterable<Grant> {
        val visited = mutableSetOf<Pair<String, String>>()
        val remaining = LinkedList(listOf(source.name to source))
        val grants = mutableSetOf<Grant>()

        while (remaining.size > 0) {
            val (from, role) = remaining.pop()

            if (!visited.add(from to role.name)) {
                throw IllegalStateException(
                    "Path $from -> ${role.name} already has been visited, " +
                        "it seems like circular dependency between roles"
                )
            }

            remaining.addAll(role.inherits.map {
                rolesByName[it] ?: throw IllegalStateException("Can't find role $it that ${role.name} depends on")
            }.map {
                role.name to it
            })

            grants.addAll(role.grants)
        }

        return grants
    }

    data class RolesServiceConfig(
        val roles: Iterable<RoleDefinition>
    )

    interface RoleDefinition {
        val name: String
        val inherits: List<String>
        val grants: List<Grant>
    }
}