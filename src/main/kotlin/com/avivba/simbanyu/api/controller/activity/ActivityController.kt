package com.avivba.simbanyu.api.controller.activity

import com.avivba.simbanyu.api.database.entity.Activity
import com.avivba.simbanyu.api.database.repository.ActivityRepository
import com.avivba.simbanyu.api.service.ActivityService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/activities")
class ActivityController(
    val activityService: ActivityService
) {
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @GetMapping
    fun getAllActivities() = activityService.getAllActivities()
}