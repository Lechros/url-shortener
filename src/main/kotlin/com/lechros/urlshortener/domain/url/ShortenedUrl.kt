package com.lechros.urlshortener.domain.url

import com.lechros.urlshortener.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
class ShortenedUrl(
    @Column(nullable = false, length = 2000)
    val targetUrl: String,

    @Column(nullable = false, length = 20)
    val shortPath: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime,

    @Column(nullable = true)
    val expiresAt: LocalDateTime? = null,

    id: Long = 0L
) : BaseEntity(id) {
    @Column(nullable = false)
    var disabled: Boolean = false
        private set

    @Column(nullable = false)
    var deleted: Boolean = false
        private set

    fun isExpiredAt(at: LocalDateTime): Boolean = expiresAt?.isBefore(at) ?: false

    fun disable() {
        disabled = true
    }

    fun enable() {
        disabled = false
    }

    fun delete() {
        deleted = true
    }
}
