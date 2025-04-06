package com.lechros.urlshortener.application

import com.lechros.urlshortener.domain.url.ShortenedUrl
import com.lechros.urlshortener.domain.url.ShortenedUrlRepository
import com.lechros.urlshortener.infra.redis.MethodFairLock
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class UrlService(
    private val shortenedUrlRepository: ShortenedUrlRepository,
) {
    lateinit var self: UrlService

    fun getUrl(alias: String): String {
        val now = LocalDateTime.now()
        val shortenedUrl = shortenedUrlRepository.findEnabledUrl(alias, now) ?: throw EntityNotFoundException()

        return shortenedUrl.url
    }

    /**
     * `alias`가 지정되었으면 해당 경로로, 아닐 경우 임의의 경로로 단축 URL을 생성합니다.
     *
     * @throws InvalidUrlException 잘못된 타겟 URL인 경우
     * @throws AliasAlreadyExistsException 이미 존재하는 단축 URL인 경우
     * @throws InvalidUrlExpireDateException 만료일이 현재 시간보다 이전인 경우
     * @throws ShortUrlCreateException 단축 URL을 생성하지 못한 경우
     */
    fun shortenUrl(request: ShortenedUrlCreateRequest): ShortenedUrlResponse {
        val url = normalizeUrl(request.url)
        validateUrl(url)

        val now = LocalDateTime.now()
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
        val result = self.tryInsert(url, alias, createdAt, expiresAt) ?: throw AliasAlreadyExistsException()
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
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
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
}
