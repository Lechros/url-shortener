package com.lechros.urlshortener.support.hashid.encoder

interface HashIdEncoder {
    fun encode(id: Long): String
}
