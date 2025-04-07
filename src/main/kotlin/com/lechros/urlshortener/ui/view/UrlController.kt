package com.lechros.urlshortener.ui.view

import com.lechros.urlshortener.application.UrlNotFoundException
import com.lechros.urlshortener.application.UrlService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@RequestMapping("/")
@Controller
class UrlController(
    private val urlService: UrlService
) {
    @GetMapping
    fun index(): String {
        return "forward:/index.html"
    }

    @GetMapping("/{alias:[0-9a-zA-Z]{1,20}}")
    fun redirect(@PathVariable alias: String): ResponseEntity<Unit> {
        val url = urlService.getUrl(alias)
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, url)
            .build()
    }

    @ExceptionHandler
    fun handleUrlNotFoundException(ex: UrlNotFoundException): ModelAndView {
        return ModelAndView("forward:/error/404.html").apply {
            status = HttpStatus.NOT_FOUND
        }
    }
}
