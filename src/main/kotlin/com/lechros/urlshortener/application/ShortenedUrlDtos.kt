package com.lechros.urlshortener.application

import com.lechros.urlshortener.domain.url.ShortenedUrl
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class ShortenedUrlCreateRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 2000)
    val url: String,

    @field:Size(max = 20)
    val alias: String? = null,

    val expiresAt: LocalDateTime? = null,
)

data class ShortenedUrlResponse(
    val url: String,
    val alias: String,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime? = null,
    val disabled: Boolean = false,
    val deleted: Boolean = false,
) {
    constructor(shortenedUrl: ShortenedUrl) : this(
        url = shortenedUrl.url,
        alias = shortenedUrl.alias,
        createdAt = shortenedUrl.createdAt,
        expiresAt = shortenedUrl.expiresAt,
        disabled = shortenedUrl.disabled,
        deleted = shortenedUrl.deleted,
    )
}
