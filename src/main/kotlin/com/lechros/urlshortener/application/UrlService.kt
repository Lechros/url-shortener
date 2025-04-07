package com.lechros.urlshortener.application

import com.lechros.urlshortener.domain.url.ShortenedUrl
import com.lechros.urlshortener.domain.url.ShortenedUrlRepository
import com.lechros.urlshortener.infra.redis.MethodFairLock
import jakarta.transaction.Transactional
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

@Service
class UrlService(
    private val shortenedUrlRepository: ShortenedUrlRepository,
) {
    @Autowired
    @Lazy
    private lateinit var self: UrlService

    /**
     * `alias`에 해당하는 단축 URL을 조회합니다.
     *
     * @throws UrlNotFoundException 단축 URL이 존재하지 않는 경우
     */
    fun getUrl(alias: String): String {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val shortenedUrl = shortenedUrlRepository.findEnabledUrl(alias, now) ?: throw UrlNotFoundException()

        return shortenedUrl.url
    }

    /**
     * `alias`가 지정되었으면 해당 경로로, 아닐 경우 임의의 경로로 단축 URL을 생성합니다.
     *
     * @throws InvalidUrlException 잘못된 타겟 URL인 경우
     * @throws InvalidAliasException 이미 존재하는 단축 URL인 경우
     * @throws InvalidUrlExpireDateException 만료일이 현재 시간보다 이전인 경우
     * @throws ShortUrlCreateException 단축 URL을 생성하지 못한 경우
     */
    fun shortenUrl(request: ShortenedUrlCreateRequest): ShortenedUrlResponse {
        val url = normalizeUrl(request.url)
        validateUrl(url)

        val now = LocalDateTime.now(ZoneOffset.UTC)
        validateExpireDate(request.expiresAt, now)

        if (!request.alias.isNullOrEmpty()) {
            return doShortenUrlToFixedAlias(url, request.alias, now, request.expiresAt)
        } else {
            return doShortenUrlToRandomAlias(url, now, request.expiresAt)
        }
    }

    private fun doShortenUrlToFixedAlias(
        url: String, alias: String, createdAt: LocalDateTime, expiresAt: LocalDateTime?
    ): ShortenedUrlResponse {
        if (!alias.matches(ShortenedUrl.ALIAS_PATTERN)) {
            throw InvalidAliasException("잘못된 alias 형식입니다.")
        }
        val result =
            self.tryInsert(url, alias, createdAt, expiresAt) ?: throw InvalidAliasException("이미 존재하는 alias입니다.")
        return ShortenedUrlResponse(result)
    }

    private fun doShortenUrlToRandomAlias(
        url: String, alias: LocalDateTime, expiresAt: LocalDateTime?
    ): ShortenedUrlResponse {
        repeat(10) {
            val generatedAlias = generateRandomBase62String(8)
            val result = self.tryInsert(url, generatedAlias, alias, expiresAt)
            if (result != null) {
                return ShortenedUrlResponse(result)
            }
        }
        // 100억개의 레코드가 존재할 때 1/15771312519340 확률
        throw ShortUrlCreateException("단축 URL 생성에 실패했습니다. 잠시 후 다시 시도해주세요.")
    }

    @MethodFairLock("url:create:#{#alias}", 1000, 500, TimeUnit.MILLISECONDS)
    @Transactional
    fun tryInsert(
        url: String, alias: String, createdAt: LocalDateTime, expiresAt: LocalDateTime?
    ): ShortenedUrl? {
        shortenedUrlRepository.findValidUrl(alias, createdAt)?.let {
            return null
        }

        val shortenedUrl = shortenedUrlRepository.save(
            ShortenedUrl(
                url = url, alias = alias, createdAt = createdAt, expiresAt = expiresAt
            )
        )
        return shortenedUrl
    }

    private fun generateRandomBase62String(length: Int): String {
        val characters = BASE62_CHARACTERS
        val randomStringBuilder = StringBuilder(length)
        repeat(length) {
            val randomIndex = (characters.indices).random()
            randomStringBuilder.append(characters[randomIndex])
        }
        return randomStringBuilder.toString()
    }

    /**
     * URL을 정규화합니다. 프로토콜이 없는 경우 http://를 추가합니다.
     */
    private fun normalizeUrl(url: String): String {
        return if (!url.contains("://")) "http://$url" else url
    }

    private fun validateUrl(url: String) {
        if (!UrlValidator.getInstance().isValid(url)) {
            throw InvalidUrlException()
        }
    }

    private fun validateExpireDate(expiresAt: LocalDateTime?, now: LocalDateTime) {
        if (expiresAt != null && expiresAt.isBefore(now)) {
            throw InvalidUrlExpireDateException()
        }
    }

    companion object {
        private const val BASE62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    }
}
