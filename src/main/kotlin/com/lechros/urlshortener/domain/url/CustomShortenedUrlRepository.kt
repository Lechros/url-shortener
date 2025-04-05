package com.lechros.urlshortener.domain.url

import java.time.LocalDateTime

interface CustomShortenedUrlRepository {
    fun findEnabledUrl(shortPath: String, currentTime: LocalDateTime): ShortenedUrl?

    fun findValidUrl(shortPath: String, currentTime: LocalDateTime): ShortenedUrl?
}
