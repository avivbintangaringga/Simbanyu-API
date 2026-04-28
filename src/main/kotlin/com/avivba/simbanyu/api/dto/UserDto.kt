package com.avivba.simbanyu.api.dto

data class UserDto(
    val id: String,
    val username: String,
    val name: String,
    val role: String,
    val phoneNumber: String,
    val photoImageURL: String? = null,
    val createdAt: String,
    val updatedAt: String,
)