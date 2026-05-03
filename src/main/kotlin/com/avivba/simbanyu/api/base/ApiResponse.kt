package com.avivba.simbanyu.api.base

sealed interface ApiResponse<T> {
    data class Success<T>(
        val status: String = "success",
        val message: String,
        val data: T
    ) : ApiResponse<T>

    data class Error<T>(
        val status: String = "error",
        val message: String,
        val errors: List<String> = emptyList()
    ) : ApiResponse<T>

    companion object {
        fun <T> success(message: String, data: T): Success<T> =
            Success(message = message, data = data)

        fun <T> error(message: String, errors: List<String> = emptyList()): Error<T> =
            Error(message = message, errors = errors)
    }
}