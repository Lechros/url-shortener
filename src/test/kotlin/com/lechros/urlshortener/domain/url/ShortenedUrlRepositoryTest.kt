package com.lechros.urlshortener.domain.url

import com.lechros.urlshortener.RepositoryTest
import com.lechros.urlshortener.createShortenedUrl
import com.linecorp.kotlinjdsl.support.spring.data.jpa.autoconfigure.KotlinJdslAutoConfiguration
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import
import java.time.LocalDateTime
import java.time.ZoneOffset

@RepositoryTest
@Import(KotlinJdslAutoConfiguration::class)
class ShortenedUrlRepositoryTest(
    private val shortenedUrlRepository: ShortenedUrlRepository,
) : ExpectSpec({
    context("활성화된 단축 URL 조회") {
        val now = LocalDateTime.now(ZoneOffset.UTC)

        shortenedUrlRepository.saveAll(
            listOf(
                createShortenedUrl(alias = "normal", url = "normal"),
                createShortenedUrl(alias = "expired", url = "expired", expiresAt = now.minusDays(1)),
                createShortenedUrl(alias = "disabled", url = "disabled", disabled = true),
                createShortenedUrl(alias = "deleted", url = "deleted", deleted = true),
            )
        )

        expect("정상적인 단축 URL이 조회되어야 한다.") {
            val actual = shortenedUrlRepository.findEnabledUrl("normal", now)
            actual shouldNotBeNull {
                url shouldBe "normal"
            }
        }

        expect("만료된 단축 URL이 조회되지 않아야 한다.") {
            val actual = shortenedUrlRepository.findEnabledUrl("expired", now)
            actual shouldBe null
        }

        expect("Disabled된 단축 URL이 조회되지 않아야 한다.") {
            val actual = shortenedUrlRepository.findEnabledUrl("disabled", now)
            actual shouldBe null
        }

        expect("삭제된 단축 URL이 조회되지 않아야 한다.") {
            val actual = shortenedUrlRepository.findEnabledUrl("deleted", now)
            actual shouldBe null
        }

        expect("존재하지 않는 단축 URL이 조회되지 않아야 한다.") {
            val actual = shortenedUrlRepository.findEnabledUrl("not-exist", now)
            actual shouldBe null
        }
    }

    context("유효한 단축 URL 조회") {
        val now = LocalDateTime.now(ZoneOffset.UTC)

        shortenedUrlRepository.saveAll(
            listOf(
                createShortenedUrl(alias = "normal", url = "normal"),
                createShortenedUrl(alias = "expired", url = "expired", expiresAt = now.minusDays(1)),
                createShortenedUrl(alias = "disabled", url = "disabled", disabled = true),
                createShortenedUrl(alias = "deleted", url = "deleted", deleted = true),
            )
        )

        expect("정상적인 단축 URL이 조회되어야 한다.") {
            val actual = shortenedUrlRepository.findValidUrl("normal", now)
            actual shouldNotBeNull {
                url shouldBe "normal"
            }
        }

        expect("만료된 단축 URL이 조회되지 않아야 한다.") {
            val actual = shortenedUrlRepository.findValidUrl("expired", now)
            actual shouldBe null
        }

        expect("Disabled된 단축 URL이 조회되어야 한다.") {
            val actual = shortenedUrlRepository.findValidUrl("disabled", now)
            actual shouldNotBeNull {
                url shouldBe "disabled"
            }
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
