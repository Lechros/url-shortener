package com.lechros.urlshortener.ui.api

import com.lechros.urlshortener.application.InvalidTargetUrlException
import com.lechros.urlshortener.application.InvalidUrlExpireDateException
import com.lechros.urlshortener.application.ShortPathAlreadyExistsException
import com.lechros.urlshortener.application.ShortUrlCreateException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        logger.error("message", ex)
        logger.error(ex.bindingResult.fieldErrors.size)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ex.bindingResult.fieldErrors.joinToString(", ") {
                "${it.field}: ${it.defaultMessage.orEmpty()}"
            })
    }

    @ExceptionHandler(ShortPathAlreadyExistsException::class)
    fun handleShortPathAlreadyExistsException(ex: ShortPathAlreadyExistsException): ResponseEntity<String> {
        logger.error("message", ex)
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ex.message)
    }

    @ExceptionHandler(InvalidTargetUrlException::class)
    fun handleInvalidTargetUrlException(ex: InvalidTargetUrlException): ResponseEntity<String> {
        logger.error("message", ex)
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ex.message)
    }

    @ExceptionHandler(InvalidUrlExpireDateException::class)
    fun handleInvalidUrlExpireDateException(ex: InvalidUrlExpireDateException): ResponseEntity<String> {
        logger.error("message", ex)
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ex.message)
    }

    @ExceptionHandler(ShortUrlCreateException::class)
    fun handleShortUrlCreateException(ex: ShortUrlCreateException): ResponseEntity<String> {
        logger.error("message", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ex.message)
    }
}
