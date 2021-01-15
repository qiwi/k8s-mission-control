package ru.qiwi.devops.mission.control.messages

interface MessagesService {
    fun create(key: String, params: Map<String, String>): Message

    fun create(key: String, vararg params: Pair<String, String>): Message {
        return create(key, params.toMap())
    }
}