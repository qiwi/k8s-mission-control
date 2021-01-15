package ru.qiwi.devops.mission.control.api

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.unboundid.ldap.listener.InMemoryDirectoryServer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import ru.qiwi.devops.mission.control.web.model.CreateSessionDTO
import ru.qiwi.devops.mission.control.web.model.SessionDTO
import ru.qiwi.devops.mission.control.utils.getForEntity

@TestPropertySource(properties = [
    "spring.ldap.embedded.ldif=classpath:test-ldap-server.ldif",
    "spring.ldap.embedded.base-dn=dc=mission-control",
    "spring.ldap.embedded.port=8389",

    "mission-control.web.auth.type=LDAP",
    "mission-control.web.auth.ldap.url=ldap://localhost:8389/",
    "mission-control.web.auth.ldap.searchBase=ou=people,dc=mission-control",
    "mission-control.web.auth.ldap.searchFilter=(&(objectClass=person)(uid={0}))",
    "mission-control.web.auth.ldap.readerUserName=uid=reader,ou=people,dc=mission-control",
    "mission-control.web.auth.ldap.readerPassword=reader"
])
class LdapAuthApiTest : BaseApiTest() {
    @Autowired
    lateinit var server: InMemoryDirectoryServer

    companion object {
        val USER_VALID = CreateSessionDTO("paul", "123", cookie = false)
        val USER_INVALID_PASSWORD = CreateSessionDTO("paul", "invalid", cookie = false)
        val USER_DELETED = CreateSessionDTO("deleted", "123", cookie = false)
        val USER_DISABLED = CreateSessionDTO("disabled", "123", cookie = false)
    }

    @Test
    fun `Should authenticate`() {
        val response = restTemplate.postForEntity<SessionDTO>("$apiUrl/sessions", USER_VALID)
        val token = response.body?.token

        assertThat(response.body?.userName).isEqualTo(USER_VALID.userName)
        assertThat(token).isNotNull()

        val session = restTemplate.getForEntity<SessionDTO>("$apiUrl/sessions/me") {
            setBearerAuth(token ?: "")
        }
        assertThat(session.body?.userName).isEqualTo(USER_VALID.userName)
    }

    @Test
    fun `Should not authorize requests from blocked user`() {
        val response = restTemplate.postForEntity<SessionDTO>("$apiUrl/sessions", USER_DELETED)
        val token = response.body?.token

        assertThat(response.body?.userName).isEqualTo(USER_DELETED.userName)
        assertThat(token).isNotNull()

        val session = restTemplate.getForEntity<SessionDTO>("$apiUrl/sessions/me") {
            setBearerAuth(token ?: "")
        }
        assertThat(session.body?.userName).isEqualTo(USER_DELETED.userName)

        server.delete("uid=${USER_DELETED.userName},ou=people,dc=mission-control")

        val session2 = restTemplate.getForEntity<String>("$apiUrl/sessions/me") {
            setBearerAuth(token ?: "")
        }
        assertThat(session2.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `Should not authenticate user with invalid password`() {
        val response = restTemplate.postForEntity<String>("$apiUrl/sessions", USER_INVALID_PASSWORD)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `Should not authenticate disabled user`() {
        val response = restTemplate.postForEntity<String>("$apiUrl/sessions", USER_DISABLED)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}