package com.avivba.simbanyu.api.features.activity

import com.avivba.simbanyu.api.base.ApiResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/activities")
class ActivityController(
    val activityService: ActivityService
) {
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @GetMapping
    fun getAllActivities(
        @AuthenticationPrincipal user: UserDetails
    ): ApiResponse<List<Activity>> {
        return activityService.getAllActivities(
            user
        )
    }
}