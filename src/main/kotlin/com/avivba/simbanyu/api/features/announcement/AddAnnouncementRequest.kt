package com.avivba.simbanyu.api.features.announcement

import jakarta.validation.constraints.NotEmpty

data class AddAnnouncementRequest(
    @NotEmpty
    val title: String,

    @NotEmpty
    val body: String,
)
