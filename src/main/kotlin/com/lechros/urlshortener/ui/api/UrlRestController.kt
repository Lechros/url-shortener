package com.lechros.urlshortener.ui.api

import com.lechros.urlshortener.PageInfo
import com.lechros.urlshortener.application.ShortenedUrlCreateRequest
import com.lechros.urlshortener.application.ShortenedUrlPageRequestDto
import com.lechros.urlshortener.application.ShortenedUrlResponse
import com.lechros.urlshortener.application.UrlService
import com.lechros.urlshortener.support.hashid.encoder.HashIdDecoder
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class UrlRestController(
    private val urlService: UrlService,
    private val decoder: HashIdDecoder,
) {
    @PostMapping("/create")
    fun shortenUrl(@RequestBody @Valid request: ShortenedUrlCreateRequest): ResponseEntity<ApiResponse<ShortenedUrlResponse>> {
        return ResponseEntity.ok(ApiResponse.success(urlService.shortenUrl(request)))
    }

    @GetMapping("/list")
    fun listUrls(@Valid pageableDto: ShortenedUrlPageRequestDto): ResponseEntity<ApiResponse<PageInfo<ShortenedUrlResponse>>> {
        return ResponseEntity.ok(ApiResponse.success(urlService.getShortenedUrlPage(pageableDto.toPageable())))
    }

    @PostMapping("/{encodedId}/enable")
    fun enableUrl(@PathVariable encodedId: String): ResponseEntity<Unit> {
        val id = decoder.decode(encodedId)
        urlService.enableShortenedUrl(id)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{encodedId}/disable")
    fun disableUrl(@PathVariable encodedId: String): ResponseEntity<Unit> {
        val id = decoder.decode(encodedId)
        urlService.disableShortenedUrl(id)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{encodedId}")
    fun deleteUrl(@PathVariable encodedId: String): ResponseEntity<Unit> {
        val id = decoder.decode(encodedId)
        urlService.deleteShortenedUrl(id)
        return ResponseEntity.ok().build()
    }
}
