package ru.qiwi.devops.mission.control.config.security

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder

interface AuthenticationConfiguration {
    fun configure(auth: AuthenticationManagerBuilder)
}