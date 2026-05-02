package com.avivba.simbanyu.api.features.activity.model

data class ActivityDto(
    val id: String,
    val title: String,
    val body: String,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun fromEntity(activity: Activity) = ActivityDto(
            id = activity.id.toString(),
            title = activity.title,
            body = activity.body,
            createdAt = activity.createdAt.toString(),
            updatedAt = activity.updatedAt.toString(),
        )
    }
}