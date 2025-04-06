package com.lechros.urlshortener.domain.url

import java.time.LocalDateTime

interface CustomShortenedUrlRepository {
    fun findEnabledUrl(alias: String, currentTime: LocalDateTime): ShortenedUrl?

    fun findValidUrl(alias: String, currentTime: LocalDateTime): ShortenedUrl?
}
