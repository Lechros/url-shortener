package com.lechros.urlshortener.support.hash

interface HashIdDecoder {
    fun decode(hash: String): Long
}
