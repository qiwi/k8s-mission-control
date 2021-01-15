package ru.qiwi.devops.mission.control.messages

import java.util.Locale

abstract class AbstractMessagesService : MessagesService {
    override fun create(key: String, params: Map<String, String>): Message {
        return InternalMessage(this, key, params)
    }

    protected abstract fun localize(key: String, params: Map<String, String>, locale: Locale): String

    private class InternalMessage(
        private val messages: AbstractMessagesService,
        override val key: String,
        override val params: Map<String, String>
    ) : Message {
        override fun getUserMessage(locale: Locale): String {
            return messages.localize(key, params, locale)
        }
    }
}