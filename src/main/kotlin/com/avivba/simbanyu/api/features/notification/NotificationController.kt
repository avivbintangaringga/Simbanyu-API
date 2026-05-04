package com.avivba.simbanyu.api.features.notification

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.notification.model.NotificationDto
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
    @ResponseMessage("Notifications retrieved successfully.")
    fun getUserNotifications(
        @AuthenticationPrincipal userDetails: UserDetails
    ): List<NotificationDto> {
        val user = userRepository.findByUsername(userDetails.username)
        return notificationService.getUserNotifications(user)
            .let {
                it.map { notification ->
                    NotificationDto.fromEntity(notification)
                }
            }
    }
}