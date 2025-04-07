package com.lechros.urlshortener.support.hashid.encoder

import com.lechros.urlshortener.support.Base62
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class HashIdEncoderImpl(
    @Value("\${hashid.salt}") private val salt: Long,
    @Value("\${hashid.round}") private val round: Int,
) : HashIdEncoder, HashIdDecoder {
    val cipher: FeistelCipher = FeistelCipher(salt.toULong(), round)

    override fun encode(id: Long): String {
        return Base62.encode(cipher.encrypt(id.toULong()))
    }

    override fun decode(hash: String): Long {
        return cipher.decrypt(Base62.decode(hash)).toLong()
    }
}
