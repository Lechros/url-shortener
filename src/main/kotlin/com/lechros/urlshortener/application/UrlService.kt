package com.lechros.urlshortener.application

import com.lechros.urlshortener.domain.url.ShortenedUrlRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UrlService(
    private val shortenedUrlRepository: ShortenedUrlRepository
) {
    fun getTargetUrl(shortPath: String): String {
        val now = LocalDateTime.now()
        val shortenedUrl =
            shortenedUrlRepository.findValidUrl(shortPath, now)
                ?: throw EntityNotFoundException()

        return shortenedUrl.targetUrl
    }
}
