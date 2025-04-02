package com.lechros.urlshortener.domain.url

import org.springframework.data.jpa.repository.JpaRepository

interface ShortenedUrlRepository : JpaRepository<ShortenedUrl, Long>, CustomShortenedUrlRepository
