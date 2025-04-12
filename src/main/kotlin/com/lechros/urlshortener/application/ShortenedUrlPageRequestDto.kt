package com.lechros.urlshortener.application

import jakarta.validation.constraints.Max
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class ShortenedUrlPageRequestDto(
    val page: Int = 0,
    @field:Max(20)
    val size: Int = 20,
) {
    fun toPageable(): Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
}
