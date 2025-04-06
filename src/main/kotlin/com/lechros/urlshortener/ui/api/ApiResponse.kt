package com.lechros.urlshortener.ui.api

class ApiResponse<T>(
    val status: String,
    val data: T? = null,
    val message: String? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse("success", data = data)
        }

        fun error(message: String?): ApiResponse<Nothing> {
            return ApiResponse("error", message = message ?: "")
        }
    }
}
