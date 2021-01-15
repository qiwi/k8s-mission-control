package ru.qiwi.devops.mission.control.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import ru.qiwi.devops.mission.control.config.security.AuthenticationConfiguration
import ru.qiwi.devops.mission.control.web.filters.ClusterHealthCheckInterceptor
import ru.qiwi.devops.mission.control.web.filters.RouteDetailsLoggingInterceptor

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class WebConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val clusterHealthCheckInterceptor: ClusterHealthCheckInterceptor,
    private val config: WebProperties
) : WebMvcConfigurer, WebSecurityConfigurerAdapter() {
    override fun addCorsMappings(registry: CorsRegistry) {
        val origins = config.cors.allowedOrigins.toTypedArray()
        registry.addMapping("/**")
            .allowedOrigins(*origins)
            .exposedHeaders("Cookie", "Set-Cookie", "Location")
            .allowCredentials(true)
            .allowedMethods("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH")
        super.addCorsMappings(registry)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(RouteDetailsLoggingInterceptor())
        registry.addInterceptor(clusterHealthCheckInterceptor)
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .formLogin().disable()
            .logout().disable()
            .rememberMe().disable()
            .anonymous().disable()
            .sessionManagement().disable()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        authenticationConfiguration.configure(auth)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}