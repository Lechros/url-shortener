package com.lechros.urlshortener.application

import com.lechros.urlshortener.createShortenedUrl
import com.lechros.urlshortener.createShortenedUrlCreateRequest
import com.lechros.urlshortener.domain.url.ShortenedUrlRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityNotFoundException
import java.time.LocalDateTime

class UrlServiceTest : BehaviorSpec({
    val shortenedUrlRepository = mockk<ShortenedUrlRepository>()

    val urlService = UrlService(shortenedUrlRepository)
    urlService.self = urlService

    Given("활성화된 단축 URL이 존재하는 경우") {
        every { shortenedUrlRepository.findEnabledUrl(any(), any()) } returns createShortenedUrl()
        every { shortenedUrlRepository.findValidUrl(any(), any()) } returns createShortenedUrl()

        When("동일한 경로로 단축 URL을 생성하면") {
            Then("예외가 발생한다") {
                shouldThrow<ShortPathAlreadyExistsException> {
                    urlService.shortenUrl(createShortenedUrlCreateRequest())
                }
            }
        }

        When("해당 경로로 조회하면") {
            val targetUrl = urlService.getTargetUrl("test1")

            Then("원본 URL이 반환된다") {
                targetUrl shouldBe "https://example.com/"
            }
        }
    }

    Given("비활성화된 단축 URL이 존재하는 경우") {
        val disabledUrl = createShortenedUrl(disabled = true)
        every { shortenedUrlRepository.findEnabledUrl(any(), any()) } returns null
        every { shortenedUrlRepository.findValidUrl(any(), any()) } returns disabledUrl

        When("동일한 경로로 단축 URL을 생성하면") {
            Then("예외가 발생한다") {
                shouldThrow<ShortPathAlreadyExistsException> {
                    urlService.shortenUrl(createShortenedUrlCreateRequest())
                }
            }
        }

        When("해당 경로로 조회하면") {
            Then("예외가 발생한다") {
                shouldThrow<EntityNotFoundException> {
                    urlService.getTargetUrl("test1")
                }
            }
        }
    }

    Given("기간이 만료된 단축 URL이 존재하는 경우") {
        every { shortenedUrlRepository.findValidUrl(any(), any()) } returns null
        every { shortenedUrlRepository.save(any()) } returnsArgument 0

        When("동일한 경로로 단축 URL을 생성하면") {
            val shortenedUrl = urlService.shortenUrl(createShortenedUrlCreateRequest())

            Then("생성에 성공한다") {
                shortenedUrl.targetUrl shouldBe "https://example.com/"
            }
        }
    }

    Given("삭제된 단축 URL이 존재하는 경우") {
        every { shortenedUrlRepository.findValidUrl(any(), any()) } returns null
        every { shortenedUrlRepository.save(any()) } returnsArgument 0

        When("동일한 경로로 단축 URL을 생성하면") {
            val shortenedUrl = urlService.shortenUrl(createShortenedUrlCreateRequest())

            Then("생성에 성공한다") {
                shortenedUrl.targetUrl shouldBe "https://example.com/"
            }
        }
    }

    Given("단축 URL이 존재하지 않는 경우") {
        every { shortenedUrlRepository.findValidUrl(any(), any()) } returns null
        every { shortenedUrlRepository.save(any()) } returnsArgument 0

        When("동일한 경로로 단축 URL을 생성하면") {
            val shortenedUrl = urlService.shortenUrl(createShortenedUrlCreateRequest())

            Then("생성에 성공한다") {
                shortenedUrl.targetUrl shouldBe "https://example.com/"
            }
        }

        When("잘못된 URL을 단축하면") {
            Then("예외가 발생한다") {
                shouldThrow<InvalidTargetUrlException> {
                    urlService.shortenUrl(createShortenedUrlCreateRequest(targetUrl = "invalid-url"))
                }
            }
        }

        When("만료 시점을 과거로 설정하여 단축하면") {
            val expireDate = LocalDateTime.now().minusDays(1)

            Then("예외가 발생한다") {
                shouldThrow<InvalidUrlExpireDateException> {
                    urlService.shortenUrl(createShortenedUrlCreateRequest(expiresAt = expireDate))
                }
            }
        }
    }
})
