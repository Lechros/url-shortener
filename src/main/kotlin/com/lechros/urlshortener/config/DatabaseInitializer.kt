package com.lechros.urlshortener.config

import com.lechros.urlshortener.domain.url.ShortenedUrl
import com.lechros.urlshortener.domain.url.ShortenedUrlRepository
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Profile("local")
@Transactional
@Component
class DatabaseInitializer(
    private val shortenedUrlRepository: ShortenedUrlRepository
) : CommandLineRunner {
    override fun run(vararg args: String?) {

        if (shouldSkip()) return
        populate()
    }

    private fun shouldSkip(): Boolean {
        return shortenedUrlRepository.count() != 0L
    }

    private fun populate() {
        populateUrls()
    }

    private fun populateUrls() {
        val urls = listOf(
            ShortenedUrl(
                "http://google.com",
                "test123",
                LocalDateTime.now()
            )
        )
        shortenedUrlRepository.saveAll(urls)
    }
}
