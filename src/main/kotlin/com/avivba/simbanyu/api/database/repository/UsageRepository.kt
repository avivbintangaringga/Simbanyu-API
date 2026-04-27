package com.avivba.simbanyu.api.database.repository

import com.avivba.simbanyu.api.database.entity.Usage
import org.springframework.data.jpa.repository.JpaRepository

interface UsageRepository : JpaRepository<Usage, String> {
}