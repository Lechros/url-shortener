package com.lechros.urlshortener.ui.api

import com.lechros.urlshortener.application.ShortenedUrlCreateRequest
import com.lechros.urlshortener.application.ShortenedUrlResponse
import com.lechros.urlshortener.application.UrlService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api")
@RestController
class UrlRestController(
    private val urlService: UrlService,
) {
    @PostMapping("/create")
    fun shortenUrl(@RequestBody @Valid request: ShortenedUrlCreateRequest): ResponseEntity<ApiResponse<ShortenedUrlResponse>> {
        return ResponseEntity.ok(ApiResponse.success(urlService.shortenUrl(request)))
    }
}
