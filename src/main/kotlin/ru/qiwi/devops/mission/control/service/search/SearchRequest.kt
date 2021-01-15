package ru.qiwi.devops.mission.control.service.search

data class SearchRequest(
    val filter: String,
    val limit: Int
)