package ru.qiwi.devops.mission.control.service.web.security

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isFailure
import org.junit.Test
import ru.qiwi.devops.mission.control.web.security.Grant
import ru.qiwi.devops.mission.control.web.security.RolesService

class RolesServiceTest {
    val service = RolesService(RolesService.RolesServiceConfig(listOf(
        role("ANONYM", grants = listOf("read_clusters", "read_deployments")),
        role("READER", grants = listOf("read_logs"), inherits = listOf("ANONYM")),
        role("DUTY", grants = listOf("restart_deployment", "scale_deployment")),
        role("ALL", inherits = listOf("READER", "DUTY"))
    )))

    @Test
    fun `Should not create service with circular dependency`() {
        val circularRoles = listOf(
            role("A", inherits = listOf("B")),
            role("B", inherits = listOf("A"))
        )

        assertThat { RolesService(RolesService.RolesServiceConfig(circularRoles)) }.isFailure()
    }

    @Test
    fun `Should return grants from all roles including dependencies`() {
        assertThat(
            service.mapRolesToAuthorities(listOf("READER", "DUTY"))
                .map { it.authority }
                .toTypedArray()
        ).containsExactly("read_logs", "read_clusters", "read_deployments", "restart_deployment", "scale_deployment")
    }

    private fun role(name: String, inherits: List<String> = emptyList(), grants: List<String> = emptyList()): RolesService.RoleDefinition {
        return object : RolesService.RoleDefinition {
            override val name = name
            override val inherits = inherits
            override val grants = grants.map { Grant.parse(it) }
        }
    }
}