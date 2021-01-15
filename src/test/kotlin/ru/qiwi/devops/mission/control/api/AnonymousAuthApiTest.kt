package ru.qiwi.devops.mission.control.api

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import ru.qiwi.devops.mission.control.config.WebProperties
import ru.qiwi.devops.mission.control.web.model.SessionDTO

class AnonymousAuthApiTest : BaseApiTest() {
    @Autowired
    lateinit var config: WebProperties

    @Test
    fun `Anonymous user should have roles`() {
        val session = restTemplate.getForEntity<SessionDTO>("$apiUrl/sessions/me")
        assertThat(session.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(session.body).isNotNull()
        assertThat(session.body!!.userName).isEqualTo(config.anonymous.name)
        assertThat(session.body!!.roles.toTypedArray()).containsExactly(*config.anonymous.roles.toTypedArray())
    }
}