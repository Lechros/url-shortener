package com.lechros.urlshortener.support.hash

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class FeistelCipherTest : StringSpec({
    val feistelCipher = FeistelCipher(806180477368839302uL, 4)

    "Feistel 암호화 후 복호화하면 원본이 반환된다" {
        forAll<ULong> { input ->
            val encrypted = feistelCipher.encrypt(input)
            val decrypted = feistelCipher.decrypt(encrypted)

            decrypted shouldBe input
        }
    }

    "Feistel 암호화 시 예상된 결과가 반환된다" {
        forAll(
            row(1uL, 8882965775497821603uL),
            row(3uL, 1038083939800381464uL),
        ) { input, expected ->
            feistelCipher.encrypt(input) shouldBe expected
        }
    }
})
