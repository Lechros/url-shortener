package com.lechros.urlshortener.domain.url

import com.lechros.urlshortener.RepositoryTest
import com.lechros.urlshortener.createShortenedUrl
import com.linecorp.kotlinjdsl.support.spring.data.jpa.autoconfigure.KotlinJdslAutoConfiguration
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import
import java.time.LocalDateTime

@RepositoryTest
@Import(KotlinJdslAutoConfiguration::class)
class ShortenedUrlRepositoryTests(
    private val shortenedUrlRepository: ShortenedUrlRepository,
) : ExpectSpec({
    context("유효한 단축 URL 조회") {
        val now = LocalDateTime.now()

        shortenedUrlRepository.saveAll(
             listOf(
                createShortenedUrl(shortPath = "normal", targetUrl = "normal"),
                createShortenedUrl(shortPath = "expired", targetUrl = "expired", expiresAt = now.minusDays(1)),
                createShortenedUrl(shortPath = "disabled", targetUrl = "disabled", disabled = true),
                createShortenedUrl(shortPath = "deleted", targetUrl = "deleted", deleted = true),
            )
        )

        expect("정상적인 단축 URL이 조회되어야 한다.") {
            val actual = shortenedUrlRepository.findValidUrl("normal", now)
            actual shouldNotBeNull {
                targetUrl shouldBe "normal"
            }
        }

        expect("만료된 단축 URL이 조회되지 않아야 한다.") {
            val actual = shortenedUrlRepository.findValidUrl("expired", now)
            actual shouldBe null
        }

        expect("Disabled된 단축 URL이 조회되지 않아야 한다.") {
            val actual = shortenedUrlRepository.findValidUrl("disabled", now)
            actual shouldBe null
        }

        expect("삭제된 단축 URL이 조회되지 않아야 한다.") {
            val actual = shortenedUrlRepository.findValidUrl("deleted", now)
            actual shouldBe null
        }

        expect("존재하지 않는 단축 URL이 조회되지 않아야 한다.") {
            val actual = shortenedUrlRepository.findValidUrl("not-exist", now)
            actual shouldBe null
        }
    }
})
