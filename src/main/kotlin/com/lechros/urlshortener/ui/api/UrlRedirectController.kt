package com.lechros.urlshortener.ui.api

import com.lechros.urlshortener.application.UrlService
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/")
@Controller
class UrlRedirectController(
    private val urlService: UrlService
) {
    @GetMapping("/{shortPath}")
    fun redirect(@PathVariable shortPath: String): ResponseEntity<Any> {
        try {
            val targetUrl = urlService.getTargetUrl(shortPath)
            return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, targetUrl)
                .build()
        } catch (e: EntityNotFoundException) {
            // TODO: Add Custom 404 Page
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}
