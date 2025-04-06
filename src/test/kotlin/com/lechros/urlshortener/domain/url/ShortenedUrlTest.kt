package com.lechros.urlshortener.domain.url

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ShortenedUrlTest : StringSpec({
    "단축 URL 생성" {
        val now = LocalDateTime.now()
        val url = ShortenedUrl("url", "alias", now)
        assertSoftly(url) {
            this.url shouldBe "url"
            alias shouldBe "alias"
            createdAt shouldBe now
            expiresAt shouldBe null
            disabled shouldBe false
            deleted shouldBe false
        }
    }

    "만료 일자가 설정되지 않은 단축 URL은 만료 여부가 거짓이다." {
        val now = LocalDateTime.now()
        val url = ShortenedUrl("url", "alias", now)
        url.isExpiredAt(now.minusDays(1)) shouldBe false
    }

    "만료 일자가 지나지 않은 URL은 만료 여부가 거짓이다." {
        val now = LocalDateTime.now()
        val expiresAt = now.plusDays(2)
        val url = ShortenedUrl("url", "alias", now, expiresAt)
        url.isExpiredAt(now.plusDays(1)) shouldBe false
    }

    "만료 일자가 지난 URL은 만료 여부가 참이다." {
        val now = LocalDateTime.now()
        val expiresAt = now.plusDays(1)
        val url = ShortenedUrl("url", "alias", now, expiresAt)
        url.isExpiredAt(now.plusDays(2)) shouldBe true
    }
})
