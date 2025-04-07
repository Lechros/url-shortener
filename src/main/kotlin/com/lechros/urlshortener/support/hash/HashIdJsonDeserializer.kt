package com.lechros.urlshortener.support.hash

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.springframework.stereotype.Component

@Component
class HashIdJsonDeserializer(
    private val decoder: HashIdDecoder,
) : StdDeserializer<Long>(Long::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Long {
        return decoder.decode(p.text)
    }
}
