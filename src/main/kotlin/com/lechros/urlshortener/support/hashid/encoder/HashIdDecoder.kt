package com.lechros.urlshortener.support.hashid.encoder

interface HashIdDecoder {
    fun decode(hash: String): Long
}
