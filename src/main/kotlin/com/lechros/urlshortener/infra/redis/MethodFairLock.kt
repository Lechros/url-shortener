package com.lechros.urlshortener.infra.redis

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class MethodFairLock(
    val nameExpr: String,
    val waitTime: Long,
    val leaseTime: Long,
    val unit: TimeUnit
)
