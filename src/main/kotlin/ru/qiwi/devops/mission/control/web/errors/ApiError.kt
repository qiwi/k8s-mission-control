package ru.qiwi.devops.mission.control.web.errors

import org.springframework.http.HttpStatus

interface ApiError {
    val httpStatus: HttpStatus
    val code: String
}