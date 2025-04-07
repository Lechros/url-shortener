package com.lechros.urlshortener.support.hash

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.springframework.stereotype.Component

@Component
class HashIdJsonSerializer(
    private val encoder: HashIdEncoder,
) : StdSerializer<Long>(Long::class.java) {
    override fun serialize(value: Long, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(encoder.encode(value))
    }
}
