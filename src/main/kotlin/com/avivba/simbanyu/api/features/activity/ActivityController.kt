package com.avivba.simbanyu.api.features.activity

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.activity.model.ActivityDto
import com.avivba.simbanyu.api.features.user.UserRepository
import org.springframework.data.repository.query.Param
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/activities")
class ActivityController(
    val activityService: ActivityService,
    val userRepository: UserRepository
) {
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @ResponseMessage("Activities retrieved successfully.")
    fun getAllActivities(
        @RequestParam(required = false) limit: Int?,
        @AuthenticationPrincipal userDetails: UserDetails
    ): List<ActivityDto> {
        val user = userRepository.findByUsername(userDetails.username)

        return activityService.getAllActivities(
            user,
            limit
        ).let {
            it.map { activity ->
                ActivityDto.fromEntity(activity)
            }
        }
    }
}