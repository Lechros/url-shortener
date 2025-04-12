package com.lechros.urlshortener

import org.springframework.data.domain.Page

data class PageInfo<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long,
) {
    companion object {
        fun <T> of(page: Page<T>): PageInfo<T> {
            return PageInfo(page.content, page.number, page.size, page.totalPages, page.totalElements)
        }
    }
}
