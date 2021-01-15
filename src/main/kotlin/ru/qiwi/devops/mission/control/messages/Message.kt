package ru.qiwi.devops.mission.control.messages

import java.util.Locale

interface Message {
    val key: String
    val params: Map<String, String>

    fun getUserMessage(locale: Locale): String
}