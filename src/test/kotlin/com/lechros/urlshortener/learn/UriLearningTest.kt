package com.lechros.urlshortener.learn

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.web.util.UriComponentsBuilder

class UriLearningTest : StringSpec({
    "Protocol이 없는 URL" {
        var url = "example.com"
        if (!url.contains("://")) {
            url = "http://$url"
        }
        val builder = UriComponentsBuilder.fromUriString(url, UriComponentsBuilder.ParserType.WHAT_WG)
        // http가 없을 경우 추가
        val input = builder.build()
        if (input.scheme == null) {
            builder.scheme("http")
        }
        val result = builder.toUriString()
        result shouldBe "http://example.com"
    }
})
