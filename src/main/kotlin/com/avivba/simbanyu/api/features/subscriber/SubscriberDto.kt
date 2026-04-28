package com.avivba.simbanyu.api.features.subscriber

import com.avivba.simbanyu.api.features.user.UserDto

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