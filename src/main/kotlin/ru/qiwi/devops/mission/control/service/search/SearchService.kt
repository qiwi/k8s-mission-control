package ru.qiwi.devops.mission.control.service.search

interface SearchService {
    fun search(request: SearchRequest): SearchResult
}