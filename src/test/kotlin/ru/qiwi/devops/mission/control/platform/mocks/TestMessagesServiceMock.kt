package ru.qiwi.devops.mission.control.platform.mocks

import ru.qiwi.devops.mission.control.messages.AbstractMessagesService
import java.util.Locale

class TestMessagesServiceMock : AbstractMessagesService() {
    override fun localize(key: String, params: Map<String, String>, locale: Locale): String {
        return "$key(${params.values.joinToString(", ")})"
    }
}