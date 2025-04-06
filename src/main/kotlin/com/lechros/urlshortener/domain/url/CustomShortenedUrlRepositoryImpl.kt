package com.lechros.urlshortener.domain.url

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import java.time.LocalDateTime

class CustomShortenedUrlRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor
) : CustomShortenedUrlRepository {
    override fun findEnabledUrl(alias: String, currentTime: LocalDateTime): ShortenedUrl? {
        return kotlinJdslJpqlExecutor.findAll {
            select(entity(ShortenedUrl::class))
                .from(entity(ShortenedUrl::class))
                .whereAnd(
                    path(ShortenedUrl::alias).eq(alias),
                    path(ShortenedUrl::expiresAt).isNull().or(path(ShortenedUrl::expiresAt).gt(currentTime)),
                    path(ShortenedUrl::disabled).eq(false),
                    path(ShortenedUrl::deleted).eq(false),
                )
        }.firstOrNull()
    }

    override fun findValidUrl(alias: String, currentTime: LocalDateTime): ShortenedUrl? {
        return kotlinJdslJpqlExecutor.findAll {
            select(entity(ShortenedUrl::class))
                .from(entity(ShortenedUrl::class))
                .whereAnd(
                    path(ShortenedUrl::alias).eq(alias),
                    path(ShortenedUrl::expiresAt).isNull().or(path(ShortenedUrl::expiresAt).gt(currentTime)),
                    path(ShortenedUrl::deleted).eq(false),
                )
        }.firstOrNull()
    }
}
