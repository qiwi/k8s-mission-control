package ru.qiwi.devops.mission.control.service.log.parser

interface LogParser<T> {
    fun parseToEntity(pod: String, container: String, log: String): T
}