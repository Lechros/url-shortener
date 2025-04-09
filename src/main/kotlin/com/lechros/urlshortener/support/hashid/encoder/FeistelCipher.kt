package com.lechros.urlshortener.support.hashid.encoder

class FeistelCipher(
    private val salt: ULong,
    private val rounds: Int,
) {
    fun encrypt(input: ULong): ULong {
        var left = input shr 32
        var right = input and MASK

        repeat(rounds) {
            val round = it
            val f = f(right, round)
            val newLeft = right
            val newRight = left xor f
            left = newLeft
            right = newRight
        }

        return (left shl 32) or (right and MASK)
    }

    fun decrypt(input: ULong): ULong {
        var left = input shr 32
        var right = input and MASK

        repeat(rounds) {
            val round = rounds - it - 1
            val f = f(right, round)
            val newRight = left
            val newLeft = right xor f
            left = newLeft
            right = newRight
        }

        return (left shl 32) or (right and MASK)
    }

    private fun f(right: ULong, round: Int): ULong {
        val shift = round % 16
        var hash = right xor salt
        hash = (hash shl shift) or (hash shr (64 - shift))
        return hash * 15543547291u + round.toUInt()
    }

    companion object {
        private const val MASK = 0xFFFFFFFFuL
    }
}
