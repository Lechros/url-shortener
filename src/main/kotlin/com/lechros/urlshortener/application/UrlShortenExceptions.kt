package com.lechros.urlshortener.application

class InvalidAliasException(message: String) : RuntimeException(message)

class InvalidUrlException(message: String? = "잘못된 URL 형식입니다.") : RuntimeException(message)

class InvalidUrlExpireDateException : RuntimeException("만료 일자는 현재 시간보다 이후여야 합니다.")

class ShortUrlCreateException(message: String? = null) : RuntimeException(message)
