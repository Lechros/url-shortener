package com.lechros.urlshortener

import com.lechros.urlshortener.domain.url.ShortenedUrl
import java.time.LocalDateTime

fun createShortenedUrl(
    shortPath: String = "test1",
    targetUrl: String = "https://example.com/",
    createdAt: LocalDateTime = LocalDateTime.now(),
    expiresAt: LocalDateTime? = null,
    disabled: Boolean = false,
    deleted: Boolean = false,
    id: Long = 0L
): ShortenedUrl {
    return ShortenedUrl(
        id = id,
        shortPath = shortPath,
        targetUrl = targetUrl,
        createdAt = createdAt,
        expiresAt = expiresAt
    ).apply {
        if (disabled) disable()
        if (deleted) delete()
    }
}
