package com.lechros.urlshortener.learn

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import org.springframework.expression.ParserContext
import org.springframework.expression.spel.SpelEvaluationException
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

class SpelLearningTest : StringSpec({
    fun convert(expr: String, vars: Map<String, Any> = mapOf()): Any? {
        val parser = SpelExpressionParser()
        val expression = parser.parseExpression(expr, ParserContext.TEMPLATE_EXPRESSION)
        val context = StandardEvaluationContext().apply { setVariables(vars) }
        return expression.getValue(context, Any::class.java)
    }

    "Template expression으로 변환이 잘 수행된다" {
        val variables = mapOf(
            "key" to "value"
        )
        forAll(
            row("normal", "normal"),
            row("with:colon", "with:colon"),
            row("#key", "#key"),
            row("#{#key}", "value"),
            row("path:and:#{#key}", "path:and:value"),
        ) { input, expected ->
            convert(input, variables) shouldBe expected
        }
    }

    "#{a} shouldThrow" {
        val expr = "#{a}"
        val variables = mapOf("a" to "c")
        shouldThrow<SpelEvaluationException> {
            convert(expr, variables)
        }
    }
})
