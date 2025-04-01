package com.lechros.urlshortener

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration
import kotlin.test.assertEquals

@SpringBootTest
class UrlShortenerApplicationTests {

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<Any, Any>

    @Test
    fun contextLoads() {
    }

    @Test
    fun redisLoads() {
        redisTemplate.opsForValue().set("test", "test value", Duration.ofSeconds(1))
        assertEquals("test value", redisTemplate.opsForValue().get("test"))
        redisTemplate.delete("test")
    }
}
