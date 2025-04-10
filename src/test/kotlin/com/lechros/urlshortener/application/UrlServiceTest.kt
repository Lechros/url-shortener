package com.lechros.urlshortener.application

import com.lechros.urlshortener.createShortenedUrl
import com.lechros.urlshortener.createShortenedUrlCreateRequest
import com.lechros.urlshortener.domain.url.ShortenedUrlRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class UrlServiceTest : BehaviorSpec({
    val shortenedUrlRepository = mockk<ShortenedUrlRepository>()

    val urlService = UrlService(shortenedUrlRepository)
    // Set self by reflection to bypass private
    val field = UrlService::class.java.getDeclaredField("self")
    field.isAccessible = true
    field.set(urlService, urlService)

    Given("활성화된 단축 URL이 존재하는 경우") {
        every { shortenedUrlRepository.findEnabledUrl(any(), any()) } returns createShortenedUrl()
        every { shortenedUrlRepository.findValidUrl(any(), any()) } returns createShortenedUrl()

        When("동일한 경로로 단축 URL을 생성하면") {
            Then("예외가 발생한다") {
                shouldThrow<InvalidAliasException> {
                    urlService.shortenUrl(createShortenedUrlCreateRequest())
                }
            }
        }

        When("해당 경로로 조회하면") {
            val url = urlService.getUrlByAlias("test1")

            Then("원본 URL이 반환된다") {
                url shouldBe "https://example.com/"
            }
        }
    }

    Given("비활성화된 단축 URL이 존재하는 경우") {
        val disabledUrl = createShortenedUrl(disabled = true)
        every { shortenedUrlRepository.findEnabledUrl(any(), any()) } returns null
        every { shortenedUrlRepository.findValidUrl(any(), any()) } returns disabledUrl

        When("동일한 경로로 단축 URL을 생성하면") {
            Then("예외가 발생한다") {
                shouldThrow<InvalidAliasException> {
                    urlService.shortenUrl(createShortenedUrlCreateRequest())
                }
            }
        }

        When("해당 경로로 조회하면") {
            Then("예외가 발생한다") {
                shouldThrow<AliasNotFoundException> {
                    urlService.getUrlByAlias("test1")
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
                shortenedUrl.url shouldBe "https://example.com/"
            }
        }
    }

    Given("삭제된 단축 URL이 존재하는 경우") {
        every { shortenedUrlRepository.findValidUrl(any(), any()) } returns null
        every { shortenedUrlRepository.save(any()) } returnsArgument 0

        When("동일한 경로로 단축 URL을 생성하면") {
            val shortenedUrl = urlService.shortenUrl(createShortenedUrlCreateRequest())

            Then("생성에 성공한다") {
                shortenedUrl.url shouldBe "https://example.com/"
            }
        }
    }

    Given("단축 URL이 존재하지 않는 경우") {
        every { shortenedUrlRepository.findValidUrl(any(), any()) } returns null
        every { shortenedUrlRepository.save(any()) } returnsArgument 0

        When("동일한 경로로 단축 URL을 생성하면") {
            val shortenedUrl = urlService.shortenUrl(createShortenedUrlCreateRequest())

            Then("생성에 성공한다") {
                shortenedUrl.url shouldBe "https://example.com/"
            }
        }
    }



    Given("잘못된 형식의 URL이 포함된 요청을") {
        When("단축하면") {
            Then("예외가 발생한다") {
                shouldThrow<InvalidUrlException> {
                    urlService.shortenUrl(createShortenedUrlCreateRequest(url = "invalid-url"))
                }
            }
        }
    }

    Given("alias에 base62 이외의 문자가 포함된 요청을") {
        forAll(
            row("abcd_efg"),
            row("ABCD*EFG"),
            row("abcd-efg"),
            row("한글"),
        ) { alias ->
            When("단축하면") {
                Then("예외가 발생한다") {
                    shouldThrow<InvalidAliasException> {
                        urlService.shortenUrl(createShortenedUrlCreateRequest(alias = alias))
                    }
                }
            }
        }
    }

    Given("alias의 길이가 0인 요청을") {
        val alias = ""

        When("단축하면") {
            val shortenedUrl = urlService.shortenUrl(createShortenedUrlCreateRequest(alias = alias))

            Then("임의의 alias로 단축된다") {
                shortenedUrl.alias shouldMatch "^[a-zA-Z0-9]{1,20}$"
            }
        }
    }

    Given("alias의 길이가 [1,20]인 요청을") {
        forAll(
            row("a"),
            row("1234567890"),
            row("12345678901234567890"),
        ) { alias ->
            When("단축하면") {
                val shortenedUrl = urlService.shortenUrl(createShortenedUrlCreateRequest(alias = alias))

                Then("입력한 alias로 설정된다") {
                    shortenedUrl.alias shouldBe alias
                    shortenedUrl.url shouldBe "https://example.com/"
                }
            }
        }
    }

    Given("alias의 길이가 20을 초과하는 요청을") {
        val alias = "123456789012345678901"

        When("단축하면") {
            Then("예외가 발생한다") {
                shouldThrow<InvalidAliasException> {
                    urlService.shortenUrl(createShortenedUrlCreateRequest(alias = alias))
                }
            }
        }
    }

    Given("만료 시점이 미래인 요청을") {
        val expireDate = LocalDateTime.now(ZoneOffset.UTC).plusDays(1)

        When("단축하면") {
            val shortenedUrl = urlService.shortenUrl(createShortenedUrlCreateRequest(expiresAt = expireDate))

            Then("정상적으로 단축된다") {
                shortenedUrl.url shouldBe "https://example.com/"
            }
        }
    }

    Given("만료 시점이 과거인 요청을") {
        val expireDate = LocalDateTime.now(ZoneOffset.UTC).minusDays(1)

        When("단축하면") {
            Then("예외가 발생한다") {
                shouldThrow<InvalidUrlExpireDateException> {
                    urlService.shortenUrl(createShortenedUrlCreateRequest(expiresAt = expireDate))
                }
            }
        }
    }

    Given("단축 URL이 존재하지 않을 때") {
        val id = 1L
        every { shortenedUrlRepository.findById(any()) } returns Optional.empty()

        When("id로 조회하면") {
            Then("예외가 발생한다") {
                shouldThrow<UrlNotFoundException> {
                    urlService.getShortenedUrlById(id)
                }
            }
        }

        When("비활성화를 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<UrlNotFoundException> {
                    urlService.disableShortenedUrl(id)
                }
            }
        }

        When("활성화를 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<UrlNotFoundException> {
                    urlService.enableShortenedUrl(id)
                }
            }
        }

        When("삭제를 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<UrlNotFoundException> {
                    urlService.deleteShortenedUrl(id)
                }
            }
        }
    }

    Given("단축 URL이 활성화 상태일 때") {
        val id = 1L
        val mockUrl = createShortenedUrl(id = id)
        every { shortenedUrlRepository.findById(id) } returns Optional.of(mockUrl)

        When("id로 조회하면") {
            val shortenedUrl = urlService.getShortenedUrlById(id)

            Then("정상적으로 조회된다") {
                shortenedUrl shouldBe mockUrl
            }
        }

        When("비활성화를 요청하면") {
            urlService.disableShortenedUrl(id)

            Then("정상적으로 비활성화된다") {
                mockUrl.disabled shouldBe true
            }
        }

        When("활성화를 요청하면") {
            urlService.enableShortenedUrl(id)

            Then("정상적으로 활성화된다") {
                mockUrl.disabled shouldBe false
            }
        }

        When("삭제를 요청하면") {
            urlService.deleteShortenedUrl(id)

            Then("정상적으로 삭제된다") {
                mockUrl.deleted shouldBe true
            }
        }
    }

    Given("단축 URL이 비활성화 상태일 때") {
        val id = 1L
        val mockUrl = createShortenedUrl(id = id, disabled = true)
        every { shortenedUrlRepository.findById(id) } returns Optional.of(mockUrl)

        When("id로 조회하면") {
            val shortenedUrl = urlService.getShortenedUrlById(id)

            Then("정상적으로 조회된다") {
                shortenedUrl shouldBe mockUrl
            }
        }

        When("비활성화를 요청하면") {
            urlService.disableShortenedUrl(id)

            Then("정상적으로 비활성화된다") {
                mockUrl.disabled shouldBe true
            }
        }

        When("활성화를 요청하면") {
            urlService.enableShortenedUrl(id)

            Then("정상적으로 활성화된다") {
                mockUrl.disabled shouldBe false
            }
        }

        When("삭제를 요청하면") {
            urlService.deleteShortenedUrl(id)

            Then("정상적으로 삭제된다") {
                mockUrl.deleted shouldBe true
            }
        }
    }

    Given("단축 URL이 만료된 상태일 때") {
        val id = 1L
        val mockUrl = createShortenedUrl(id = id, expiresAt = LocalDateTime.now(ZoneOffset.UTC).minusDays(1))
        every { shortenedUrlRepository.findById(id) } returns Optional.of(mockUrl)

        When("id로 조회하면") {
            val shortenedUrl = urlService.getShortenedUrlById(id)

            Then("정상적으로 조회된다") {
                shortenedUrl shouldBe mockUrl
            }
        }

        When("비활성화를 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<UrlNotFoundException> {
                    urlService.disableShortenedUrl(id)
                }
            }
        }

        When("활성화를 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<UrlNotFoundException> {
                    urlService.enableShortenedUrl(id)
                }
            }
        }

        When("삭제를 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<UrlNotFoundException> {
                    urlService.deleteShortenedUrl(id)
                }
            }
        }
    }

    Given("단축 URL이 삭제된 상태일 때") {
        val id = 1L
        val mockUrl = createShortenedUrl(id = id, deleted = true)
        every { shortenedUrlRepository.findById(id) } returns Optional.of(mockUrl)

        When("id로 조회하면") {
            val shortenedUrl = urlService.getShortenedUrlById(id)

            Then("정상적으로 조회된다") {
                shortenedUrl shouldBe mockUrl
            }
        }

        When("비활성화를 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<UrlNotFoundException> {
                    urlService.disableShortenedUrl(id)
                }
            }
        }

        When("활성화를 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<UrlNotFoundException> {
                    urlService.enableShortenedUrl(id)
                }
            }
        }

        When("삭제를 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<UrlNotFoundException> {
                    urlService.deleteShortenedUrl(id)
                }
            }
        }
    }
})
