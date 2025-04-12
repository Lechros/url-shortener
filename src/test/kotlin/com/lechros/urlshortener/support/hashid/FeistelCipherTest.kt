package com.lechros.urlshortener.support.hashid

import com.lechros.urlshortener.support.hashid.encoder.FeistelCipher
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class FeistelCipherTest : StringSpec({
    val feistelCipher = FeistelCipher(806180477368839302uL, 4)

    "Feistel 암호화 후 복호화하면 원본이 반환된다" {
        forAll(
            row(1uL),
            row(3uL),
            row(17uL),
            row(24uL),
            row(100uL),
            row(9999999999999999999uL),
        ) { input ->
            val encrypted = feistelCipher.encrypt(input)
            val decrypted = feistelCipher.decrypt(encrypted)

            decrypted shouldBe input
        }
    }

    "Feistel 암호화 시 예상된 결과가 반환된다" {
        forAll(
            row(1uL, 8882965776513927401uL),
            row(3uL, 5376012202367020031uL),
        ) { input, expected ->
            feistelCipher.encrypt(input) shouldBe expected
        }
    }

    "Feistel 복호화 시 예상된 결과가 반환된다" {
        forAll(
            row(8882965776513927401uL, 1uL),
            row(5376012202367020031uL, 3uL),
        ) { input, expected ->
            feistelCipher.decrypt(input) shouldBe expected
        }
    }
})
