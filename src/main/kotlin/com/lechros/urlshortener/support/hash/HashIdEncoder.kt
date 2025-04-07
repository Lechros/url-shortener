package com.lechros.urlshortener.support.hash

interface HashIdEncoder {
    fun encode(id: Long): String
}
