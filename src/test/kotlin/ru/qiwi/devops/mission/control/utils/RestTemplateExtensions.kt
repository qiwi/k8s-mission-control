package ru.qiwi.devops.mission.control.utils

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

inline fun <reified T> TestRestTemplate.getForEntity(url: String, headers: HttpHeaders.() -> Unit): ResponseEntity<T> {
    val request = HttpEntity<String>(
        HttpHeaders().apply(headers)
    )
    return restTemplate.exchange(url, HttpMethod.GET, request, T::class.java)
}

inline fun <reified T> TestRestTemplate.postForEntity(url: String, body: Any, headers: HttpHeaders.() -> Unit): ResponseEntity<T> {
    val request = HttpEntity(
        body,
        HttpHeaders().apply(headers)
    )
    return restTemplate.exchange(url, HttpMethod.POST, request, T::class.java)
}