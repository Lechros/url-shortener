package com.lechros.urlshortener.application

import com.lechros.urlshortener.domain.url.ShortenedUrl
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class ShortenedUrlCreateRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 2000)
    val targetUrl: String,

    @field:Size(max = 20)
    val shortPath: String? = null,

    @field:Future
    val expiresAt: LocalDateTime? = null,
)

data class ShortenedUrlResponse(
    val targetUrl: String,
    val shortPath: String,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime? = null,
    val disabled: Boolean = false,
    val deleted: Boolean = false,
) {
    constructor(shortenedUrl: ShortenedUrl) : this(
        targetUrl = shortenedUrl.targetUrl,
        shortPath = shortenedUrl.shortPath,
        createdAt = shortenedUrl.createdAt,
        expiresAt = shortenedUrl.expiresAt,
        disabled = shortenedUrl.disabled,
        deleted = shortenedUrl.deleted,
    )
}
