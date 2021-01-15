package ru.qiwi.devops.mission.control.service.search

data class SearchResult(
    val items: List<SearchResultItem<*>>,
    val totalCount: Int
)