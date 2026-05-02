package com.avivba.simbanyu.api.features.announcement.model

import jakarta.validation.constraints.NotEmpty

data class AnnouncementRequest(
    @NotEmpty
    val title: String,

    @NotEmpty
    val body: String,
)
