package com.lechros.urlshortener.ui.api

import com.lechros.urlshortener.application.*
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
    ): ResponseEntity<Any> {
        logger.error("message", ex)
        val message = ex.bindingResult.fieldErrors.joinToString(", ") {
            "${it.field}: ${it.defaultMessage.orEmpty()}"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(message))
    }

    @ExceptionHandler(InvalidAliasException::class)
    fun handleAliasAlreadyExistsException(ex: InvalidAliasException): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("message", ex)
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.message))
    }

    @ExceptionHandler(InvalidUrlException::class)
    fun handleInvalidUrlException(ex: InvalidUrlException): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("message", ex)
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ApiResponse.error(ex.message))
    }

    @ExceptionHandler(InvalidUrlExpireDateException::class)
    fun handleInvalidUrlExpireDateException(ex: InvalidUrlExpireDateException): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("message", ex)
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ApiResponse.error(ex.message))
    }

    @ExceptionHandler(ShortUrlCreateException::class)
    fun handleShortUrlCreateException(ex: ShortUrlCreateException): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("message", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(ex.message))
    }

    @ExceptionHandler(UrlNotFoundException::class)
    fun handleUrlNotFoundException(ex: UrlNotFoundException): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("message", ex)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.message))
    }
}
