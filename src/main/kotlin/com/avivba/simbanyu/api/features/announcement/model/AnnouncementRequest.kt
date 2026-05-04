package com.avivba.simbanyu.api.features.announcement.model

import jakarta.validation.constraints.NotEmpty

data class AnnouncementRequest(
    @field:NotEmpty
    val title: String,

    @field:NotEmpty
    val body: String,
)
