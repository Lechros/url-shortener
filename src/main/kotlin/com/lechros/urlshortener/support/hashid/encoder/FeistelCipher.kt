package com.lechros.urlshortener.support.hashid.encoder

class FeistelCipher(
    private val salt: ULong,
    private val rounds: Int,
) {
    fun encrypt(input: ULong): ULong {
        var left = input shr 32
        var right = input and MASK

        repeat(rounds) { round ->
            val f = f(right, round) and MASK
            val temp = right
            right = left xor f
            left = temp
        }

        return (left shl 32) or (right and MASK)
    }

    fun decrypt(input: ULong): ULong {
        var left = input shr 32
        var right = input and MASK

        for (round in (rounds - 1) downTo 0) {
            val f = f(left, round) and MASK
            val temp = left
            left = right xor f
            right = temp
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
