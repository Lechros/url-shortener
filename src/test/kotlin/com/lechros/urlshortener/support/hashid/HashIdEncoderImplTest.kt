package com.lechros.urlshortener.support.hashid

import com.lechros.urlshortener.support.hashid.encoder.HashIdEncoderImpl
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class HashIdEncoderImplTest : StringSpec({
    val encoder = HashIdEncoderImpl(806180477368839302L, 4)

    "인코딩 후 디코딩하면 원본이 반환된다" {
        forAll<Long> { input ->
            val encrypted = encoder.encode(input)
            val decrypted = encoder.decode(encrypted)

            decrypted shouldBe input
        }
    }

    "인코딩 시 예상된 결과가 반환된다" {
        forAll(
            row(100L, "3sKMSdgzfVO"),
        ) { input, expected ->
            encoder.encode(input) shouldBe expected
        }
    }

    "디코딩 시 예상된 결과가 반환된다" {
        forAll(
            row("3sKMSdgzfVO", 100L),
        ) { input, expected ->
            encoder.decode(input) shouldBe expected
        }
    }
})
