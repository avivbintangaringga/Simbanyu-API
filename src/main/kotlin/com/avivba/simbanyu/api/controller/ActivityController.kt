package com.avivba.simbanyu.api.controller

import com.avivba.simbanyu.api.database.entity.Activity
import com.avivba.simbanyu.api.database.repository.ActivityRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/activities")
class ActivityController(
    val activityRepository: ActivityRepository
) {
    @GetMapping
    fun getAllActivities() = activityRepository.findAll()

    @GetMapping("/{id}")
    fun getActivityById(@PathVariable id: String) = activityRepository.findById(id)

    @PostMapping
    fun createActivity(@RequestBody activity: Activity) = activityRepository.save(activity)

}