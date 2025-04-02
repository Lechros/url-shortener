package com.lechros.urlshortener.domain.url

import java.time.LocalDateTime

interface CustomShortenedUrlRepository {
    fun findValidUrl(shortPath: String, currentTime: LocalDateTime): ShortenedUrl?
}
