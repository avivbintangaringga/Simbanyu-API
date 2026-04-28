package com.avivba.simbanyu.api.base

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T?
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse("success", "Success", data)
        fun <T> error(): ApiResponse<T> = ApiResponse("error", "Error", null)
    }
}