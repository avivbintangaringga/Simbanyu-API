package com.avivba.simbanyu.api.dto

data class SubscriberDto(
    val id: String,
    val meterId: String,
    val subscriberType: String,
    val address: String,
    val longitude: Double,
    val latitude: Double,
    val user: UserDto,
    val createdAt: String,
    val updatedAt: String,
)
