package com.lechros.urlshortener.ui.view

import com.lechros.urlshortener.application.AliasNotFoundException
import com.lechros.urlshortener.application.UrlService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@RequestMapping("/")
@Controller
class UrlController(
    @Value("\${frontend.url}") private val frontendUrl: String,
    private val urlService: UrlService
) {
    @GetMapping("/{alias:[0-9A-Za-z]{1,20}}")
    fun redirect(@PathVariable alias: String): ResponseEntity<Unit> {
        val url = urlService.getUrlByAlias(alias)
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, url)
            .build()
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleUrlNotFoundException(ex: AliasNotFoundException, model: Model): String {
        model.addAttribute("frontendUrl", frontendUrl)
        return "error/404"
    }
}
