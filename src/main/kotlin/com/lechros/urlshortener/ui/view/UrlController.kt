package com.lechros.urlshortener.ui.view

import com.lechros.urlshortener.application.UrlNotFoundException
import com.lechros.urlshortener.application.UrlService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@RequestMapping("/")
@Controller
class UrlController(
    private val urlService: UrlService
) {
    @GetMapping
    @ResponseBody
    fun index(): String {
        return html("index.html")
    }

    @GetMapping("/{alias}")
    fun redirect(@PathVariable alias: String): ResponseEntity<Unit> {
        val url = urlService.getUrl(alias)
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, url)
            .build()
    }

    @ExceptionHandler
    fun handleUrlNotFoundException(ex: UrlNotFoundException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(html("error/404.html"))
    }

    private fun html(path: String): String {
        return this::class.java.getResource("/static/$path")?.readText(Charsets.UTF_8) ?: ""
    }
}
