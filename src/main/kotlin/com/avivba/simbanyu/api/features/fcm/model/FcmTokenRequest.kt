package com.avivba.simbanyu.api.features.fcm.model

import jakarta.validation.constraints.NotBlank

data class FcmTokenRequest(
    @NotBlank
    val token: String
)
