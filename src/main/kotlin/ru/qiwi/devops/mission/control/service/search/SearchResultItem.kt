package ru.qiwi.devops.mission.control.service.search

interface SearchResultItem<T> : Comparable<SearchResultItem<*>> {
    val score: Int
    val type: String
    val title: String
    val value: T

    override fun compareTo(other: SearchResultItem<*>): Int {
        var i: Int = this.score.compareTo(other.score) * -1
        if (i != 0) return i
        i = title.compareTo(other.title)
        return i
    }
}