package com.avivba.simbanyu.api.features.subscriber

import com.avivba.simbanyu.api.features.plans.PlanDto
import com.avivba.simbanyu.api.features.user.UserDto

data class SubscriberDto(
    val id: String,
    val meterId: String,
    val address: String,
    val longitude: Double,
    val latitude: Double,
    val isRecorded: Boolean = false,
    val lastRecordedAt: String? = null,
    val plan: PlanDto,
    val user: UserDto,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun fromEntity(subscriber: Subscriber) = SubscriberDto(
            id = subscriber.id.toString(),
            meterId = subscriber.meterId,
            address = subscriber.address,
            longitude = subscriber.longitude,
            latitude = subscriber.latitude,
            plan = PlanDto.fromEntity(subscriber.plan),
            user = UserDto.fromEntity(subscriber.user),
            createdAt = subscriber.createdAt.toString(),
            updatedAt = subscriber.updatedAt.toString(),
        )
    }
}