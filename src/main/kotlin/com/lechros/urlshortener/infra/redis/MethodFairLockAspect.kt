package com.lechros.urlshortener.infra.redis

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.springframework.core.annotation.Order
import org.springframework.expression.ParserContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component

@Aspect
@Component
@Order(-1)
class MethodFairLockAspect(
    private val redissonClient: RedissonClient,
) {
    private val parser = SpelExpressionParser()

    @Around("@annotation(com.lechros.urlshortener.infra.redis.MethodFairLock)")
    fun lock(proceedingJoinPoint: ProceedingJoinPoint): Any? {
        val signature = proceedingJoinPoint.signature as MethodSignature
        val annotation = signature.method.getAnnotation(MethodFairLock::class.java)

        val lockName = replaceParameterArgs(annotation.nameExpr, signature.parameterNames, proceedingJoinPoint.args)
        val lock = redissonClient.getFairLock(lockName)
        try {
            if (lock.tryLock(annotation.waitTime, annotation.leaseTime, annotation.unit)) {
                return proceedingJoinPoint.proceed()
            } else {
                throw LockAcquireException("Failed to acquire lock for method: ${signature.method.name}")
            }
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }

    fun replaceParameterArgs(str: String, parameterNames: Array<String>, args: Array<Any?>): String? {
        val context = StandardEvaluationContext()
        for (i in parameterNames.indices) {
            context.setVariable(parameterNames[i], args[i])
        }
        return parser.parseExpression(str, ParserContext.TEMPLATE_EXPRESSION).getValue(context, String::class.java)
    }
}
