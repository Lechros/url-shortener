package com.lechros.urlshortener.support.hash

object Base62 {
    fun encode(number: ULong): String {
        if (number == 0uL) return "0"

        val sb = StringBuilder()
        var num = number

        while (num != 0uL) {
            sb.append(CHARACTER_SET[(num % BASE).toInt()])
            num /= BASE
        }

        return sb.reverse().toString()
    }

    fun decode(encoded: String): ULong {
        require(encoded.isNotEmpty()) { "Encoded string must not be empty" }

        var result = 0uL
        for (char in encoded) {
            val index = CHAR_TO_INDEX[char] ?: throw IllegalArgumentException("Invalid Base62 character: '$char'")
            result = result * BASE + index.toUInt()
        }

        return result
    }

    private const val CHARACTER_SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private val BASE = CHARACTER_SET.length.toUInt()
    private val CHAR_TO_INDEX = CHARACTER_SET.withIndex().associate { it.value to it.index }
}
