package com.lechros.urlshortener

import com.fasterxml.jackson.databind.ObjectMapper
import com.lechros.urlshortener.support.hash.HashIdDecoder
import com.lechros.urlshortener.support.hash.HashIdEncoder
import com.lechros.urlshortener.support.hash.JsonHashId
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary

@IntegrationTest
@Import(HashIdTestConfig::class)
class JsonHashIdIntegrationTest(
    private val objectMapper: ObjectMapper,
) : StringSpec({
    "@JsonHashId가 붙은 Long 필드는 문자열로 직렬화된다" {
        val dto = HashIdTestDto(100L, 100L)

        val json = objectMapper.writeValueAsString(dto)

        json shouldBe """{"id":100,"hashId":"hash_100"}"""
    }

    "@JsonHashId가 붙은 필드는 Long으로 역직렬화된다" {
        val json = """{"id":100,"hashId":"hash_100"}"""

        val dto = objectMapper.readValue(json, HashIdTestDto::class.java)

        dto shouldBe HashIdTestDto(100L, 100L)
    }
})

data class HashIdTestDto(
    val id: Long,

    @field:JsonHashId
    val hashId: Long,
)

class HashIdTestEncoderImpl : HashIdEncoder, HashIdDecoder {
    override fun encode(id: Long): String {
        return "hash_$id"
    }

    override fun decode(hash: String): Long {
        return hash.replace("hash_", "").toLong()
    }
}

@TestConfiguration
class HashIdTestConfig {
    @Bean
    @Primary
    fun hashIdEncoder(): HashIdTestEncoderImpl {
        return HashIdTestEncoderImpl()
    }
}
