package ru.qiwi.devops.mission.control.messages

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class MessageSourceMessagesService(
    private val messageSource: MessageSource
) : AbstractMessagesService() {
    override fun localize(key: String, params: Map<String, String>, locale: Locale): String {
        return messageSource.getMessage(key, params.values.toTypedArray(), locale)
    }
}