package com.lechros.urlshortener

import com.lechros.urlshortener.application.ShortenedUrlCreateRequest
import com.lechros.urlshortener.application.ShortenedUrlResponse
import com.lechros.urlshortener.domain.url.ShortenedUrl
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val ALIAS = "test1"
private const val URL = "https://example.com/"

fun createShortenedUrl(
    alias: String = ALIAS,
    url: String = URL,
    createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),
    expiresAt: LocalDateTime? = null,
    disabled: Boolean = false,
    deleted: Boolean = false,
    id: Long = 0L
): ShortenedUrl {
    return ShortenedUrl(
        id = id,
        alias = alias,
        url = url,
        createdAt = createdAt,
        expiresAt = expiresAt
    ).apply {
        if (disabled) disable()
        if (deleted) delete()
    }
}

fun createShortenedUrlCreateRequest(
    alias: String = ALIAS,
    url: String = URL,
    expiresAt: LocalDateTime? = null,
): ShortenedUrlCreateRequest {
    return ShortenedUrlCreateRequest(
        alias = alias,
        url = url,
        expiresAt = expiresAt
    )
}

fun createShortenedUrlResponse(
    alias: String = ALIAS,
    url: String = URL,
    createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),
    expiresAt: LocalDateTime? = null,
    disabled: Boolean = false,
): ShortenedUrlResponse {
    return ShortenedUrlResponse(
        alias = alias,
        url = url,
        createdAt = createdAt,
        expiresAt = expiresAt,
        disabled = disabled,
    )
}
