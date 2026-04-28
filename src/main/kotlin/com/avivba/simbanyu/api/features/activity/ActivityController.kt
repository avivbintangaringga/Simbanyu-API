package com.avivba.simbanyu.api.features.activity

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.features.user.UserRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/activities")
class ActivityController(
    val activityService: ActivityService,
    val userRepository: UserRepository
) {
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @GetMapping
    fun getAllActivities(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ApiResponse<List<ActivityDto>> {
        val user = userRepository.findByUsername(userDetails.username)

        return activityService.getAllActivities(
            user
        ).let {
            ApiResponse.success(
                it.map { activity ->
                    ActivityDto.fromEntity(activity)
                }
            )
        }
    }
}