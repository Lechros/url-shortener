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

    fun getTargetUrl(shortPath: String): String {
        val now = LocalDateTime.now()
        val shortenedUrl = shortenedUrlRepository.findEnabledUrl(shortPath, now) ?: throw EntityNotFoundException()

        return shortenedUrl.targetUrl
    }

    /**
     * `shortPath`가 지정되었으면 해당 경로로, 아닐 경우 임의의 경로로 단축 URL을 생성합니다.
     *
     * @throws InvalidTargetUrlException 잘못된 타겟 URL인 경우
     * @throws ShortPathAlreadyExistsException 이미 존재하는 단축 URL인 경우
     * @throws InvalidUrlExpireDateException 만료일이 현재 시간보다 이전인 경우
     * @throws ShortUrlCreateException 단축 URL을 생성하지 못한 경우
     */
    fun shortenUrl(request: ShortenedUrlCreateRequest): ShortenedUrlResponse {
        val targetUrl = normalizeUrl(request.targetUrl)
        validateUrl(targetUrl)

        val now = LocalDateTime.now()
        validateExpireDate(request.expiresAt, now)

        if (!request.shortPath.isNullOrEmpty()) {
            return doShortenUrlToFixedShortPath(targetUrl, request.shortPath, now, request.expiresAt)
        } else {
            return doShortenUrlToRandomShortPath(targetUrl, now, request.expiresAt)
        }
    }

    private fun doShortenUrlToFixedShortPath(
        targetUrl: String, shortPath: String, createdAt: LocalDateTime, expiresAt: LocalDateTime?
    ): ShortenedUrlResponse {
        val result =
            self.tryInsert(targetUrl, shortPath, createdAt, expiresAt) ?: throw ShortPathAlreadyExistsException()
        return ShortenedUrlResponse(result)
    }

    private fun doShortenUrlToRandomShortPath(
        targetUrl: String, createdAt: LocalDateTime, expiresAt: LocalDateTime?
    ): ShortenedUrlResponse {
        repeat(10) {
            val generatedShortPath = generateRandomBase62String(8)
            val result = self.tryInsert(targetUrl, generatedShortPath, createdAt, expiresAt)
            if (result != null) {
                return ShortenedUrlResponse(result)
            }
        }
        // 100억개의 레코드가 존재할 때 1/15771312519340 확률
        throw ShortUrlCreateException("단축 URL 생성에 실패했습니다. 잠시 후 다시 시도해주세요.")
    }

    @MethodFairLock("url:create:#{#shortPath}", 1000, 500, TimeUnit.MILLISECONDS)
    @Transactional
    fun tryInsert(
        targetUrl: String, shortPath: String, createdAt: LocalDateTime, expiresAt: LocalDateTime?
    ): ShortenedUrl? {
        shortenedUrlRepository.findValidUrl(shortPath, createdAt)?.let {
            return null
        }

        val shortenedUrl = shortenedUrlRepository.save(
            ShortenedUrl(
                targetUrl = targetUrl, shortPath = shortPath, createdAt = createdAt, expiresAt = expiresAt
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
            throw InvalidTargetUrlException()
        }
    }

    private fun validateExpireDate(expiresAt: LocalDateTime?, now: LocalDateTime) {
        if (expiresAt != null && expiresAt.isBefore(now)) {
            throw InvalidUrlExpireDateException()
        }
    }
}
