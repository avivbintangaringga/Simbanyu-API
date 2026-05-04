package com.avivba.simbanyu.api.features.fcm

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.fcm.model.FcmTokenRequest
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/fcmtoken")
class FcmTokenController(
    private val fcmTokenService: FcmTokenService
) {
    @PostMapping
    @ResponseMessage("FCM token submitted successfully.")
    fun saveFcmToken(
        @Valid @RequestBody body: FcmTokenRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ) {
        fcmTokenService.submitToken(
            body.token,
            userDetails.username
        )
    }
}