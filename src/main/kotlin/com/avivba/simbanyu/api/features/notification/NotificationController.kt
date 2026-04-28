package com.avivba.simbanyu.api.features.notification

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.features.user.UserRepository
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService,
    private val userRepository: UserRepository
) {
    @GetMapping
    fun getUserNotifications(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ApiResponse<List<Notification>> {
        val user = userRepository.findByUsername(userDetails.username)
        return notificationService.getUserNotifications(user)
            .let { ApiResponse.success(it) }
    }
}