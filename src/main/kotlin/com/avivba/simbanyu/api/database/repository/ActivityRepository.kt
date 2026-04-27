package com.avivba.simbanyu.api.database.repository

import com.avivba.simbanyu.api.database.entity.Activity
import org.springframework.data.jpa.repository.JpaRepository

interface ActivityRepository : JpaRepository<Activity, String> {
}