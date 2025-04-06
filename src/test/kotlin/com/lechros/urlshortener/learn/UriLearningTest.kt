package com.lechros.urlshortener.learn

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import org.apache.commons.validator.routines.UrlValidator
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

    "URL Validation" {
        forAll(
            row("a", false),
            row("example.com", true),
            row("example-com", false),
            row("http://example", false),
            row("http://example.com", true),
            row("http://example.com", true),
            row("http://xn--fsq.com", true),
            row("file://a.png", false),
            row("https://xn--220b31d95hq8o.xn--3e0b707e/", true),
            row("내도메인.한국", true),
        ) { input, expected ->
            var url = input
            if (!input.contains("://")) {
                url = "http://$input"
            }
            val actual = UrlValidator.getInstance().isValid(url)
            actual shouldBe expected
        }
    }
})
