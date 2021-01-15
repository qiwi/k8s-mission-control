package ru.qiwi.devops.mission.control.utils

import org.springframework.http.MediaType

object Mime2FileTypeMap {
    private const val APPLICATION_JAVASCRIPT_VALUE = "application/javascript"
    private val APPLICATION_JAVASCRIPT = MediaType.parseMediaType(APPLICATION_JAVASCRIPT_VALUE)

    private const val FONT_WOFF_VALUE = "font/woff"
    private val FONT_WOFF = MediaType.parseMediaType(FONT_WOFF_VALUE)

    private const val FONT_WOFF2_VALUE = "font/woff2"
    private val FONT_WOFF2 = MediaType.parseMediaType(FONT_WOFF2_VALUE)

    private val mapping = mapOf(
        ".html" to MediaType.TEXT_HTML,
        ".js" to APPLICATION_JAVASCRIPT,
        ".map" to APPLICATION_JAVASCRIPT,
        ".json" to MediaType.APPLICATION_JSON,
        ".png" to MediaType.IMAGE_PNG,
        ".txt" to MediaType.TEXT_PLAIN,
        ".woff" to FONT_WOFF,
        ".woff2" to FONT_WOFF2
    )

    fun getMediaType(fileName: String): MediaType? {
        val extension = getExtension(fileName)
        return mapping[extension]
    }

    private fun getExtension(filename: String): String? {
        val i = filename.lastIndexOf(".")
        return if (i == -1) {
            null
        } else {
            filename.substring(i)
        }
    }
}