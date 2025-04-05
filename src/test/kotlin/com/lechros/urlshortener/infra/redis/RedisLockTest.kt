package com.lechros.urlshortener.infra.redis

import com.lechros.urlshortener.IntegrationTest
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.Order
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.Ordered
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@IntegrationTest
@Import(RedissonClientMockkConfig::class)
class RedisLockTest(
    private val redisLockTestService: RedisLockTestService,
    private val redissonClient: RedissonClient
) : ExpectSpec({
    context("@MethodFairLock이 설정된 메소드 호출") {
        val lock = mockk<RLock>()
        every { redissonClient.getFairLock("test:key") } returns lock
        every { lock.tryLock(1000, 500, TimeUnit.MILLISECONDS) } returns true
        every { lock.isHeldByCurrentThread } returns true
        every { lock.unlock() } just Runs

        val result = redisLockTestService.testMethod("key")

        expect("메소드가 정상적으로 실행되어야 한다") {
            result shouldBe "modified_key"
        }

        expect("RedissonClient.getFairLock이 1회 호출되어야 한다") {
            verify(exactly = 1) {
                redissonClient.getFairLock("test:key")
            }
        }

        expect("RLock.tryLock이 1회 호출되어야 한다") {
            verify(exactly = 1) {
                lock.tryLock(1000, 500, TimeUnit.MILLISECONDS)
            }
        }

        expect("RLock.unlock이 1회 호출되어야 한다") {
            verify(exactly = 1) {
                lock.unlock()
            }
        }
    }
})

@Service
class RedisLockTestService {
    @MethodFairLock("test:#{#name}", 1000, 500, TimeUnit.MILLISECONDS)
    fun testMethod(name: String): String {
        Thread.sleep(100)
        return "modified_$name"
    }
}

@TestConfiguration
@Order(Ordered.HIGHEST_PRECEDENCE)
class RedissonClientMockkConfig {
    @Bean
    fun redissonClient(): RedissonClient {
        return mockk<RedissonClient>()
    }
}
