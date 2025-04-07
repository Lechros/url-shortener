package com.lechros.urlshortener.support

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class Base62Test : StringSpec({
    "Base62 인코딩" {
        forAll(
            row(1uL, "1"),
            row(8882965775497821603uL, "AaC4nso8CTD"),
            row(1038083939800381464uL, "1EgQztjEJrs"),
        ) { input, expected ->
            Base62.encode(input) shouldBe expected
        }
    }
})
