package com.lechros.urlshortener.infra.redis

class LockAcquireException(message: String? = null) : RuntimeException(message)
