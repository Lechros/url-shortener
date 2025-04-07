package com.lechros.urlshortener.support.hashid.jackson

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = HashIdJsonSerializer::class)
@JsonDeserialize(using = HashIdJsonDeserializer::class)
annotation class JsonHashId
