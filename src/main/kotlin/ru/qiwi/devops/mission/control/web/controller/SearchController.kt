package ru.qiwi.devops.mission.control.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.qiwi.devops.mission.control.service.search.SearchRequest
import ru.qiwi.devops.mission.control.service.search.SearchResultItem
import ru.qiwi.devops.mission.control.service.search.SearchService
import ru.qiwi.devops.mission.control.utils.getLogger
import ru.qiwi.devops.mission.control.web.model.ApiResponseEntity
import ru.qiwi.devops.mission.control.web.model.toResponseEntity

@RestController
@RequestMapping("/api/search")
class SearchController(
    private val searchService: SearchService
) {
    private val logger = getLogger<SearchController>()

    @GetMapping()
    fun findPod(
        @RequestParam filter: String
    ): ApiResponseEntity<List<SearchResultItem<*>>> {
        logger.info("Looking for items with filter $filter")

        return searchService.search(SearchRequest(filter, 8)).items.toResponseEntity()
    }
}