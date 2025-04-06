package com.lechros.urlshortener

import com.lechros.urlshortener.application.ShortenedUrlCreateRequest
import com.lechros.urlshortener.application.ShortenedUrlResponse
import com.lechros.urlshortener.domain.url.ShortenedUrl
import java.time.LocalDateTime

private const val SHORT_PATH = "test1"
private const val TARGET_URL = "https://example.com/"

fun createShortenedUrl(
    shortPath: String = SHORT_PATH,
    targetUrl: String = TARGET_URL,
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

fun createShortenedUrlCreateRequest(
    shortPath: String = SHORT_PATH,
    targetUrl: String = TARGET_URL,
    expiresAt: LocalDateTime? = null,
): ShortenedUrlCreateRequest {
    return ShortenedUrlCreateRequest(
        shortPath = shortPath,
        targetUrl = targetUrl,
        expiresAt = expiresAt
    )
}

fun createShortenedUrlResponse(
    shortPath: String = SHORT_PATH,
    targetUrl: String = TARGET_URL,
    createdAt: LocalDateTime = LocalDateTime.now(),
    expiresAt: LocalDateTime? = null,
    disabled: Boolean = false,
): ShortenedUrlResponse {
    return ShortenedUrlResponse(
        shortPath = shortPath,
        targetUrl = targetUrl,
        createdAt = createdAt,
        expiresAt = expiresAt,
        disabled = disabled,
    )
}
