package com.avivba.simbanyu.api.features.usage

import org.springframework.data.jpa.repository.JpaRepository

interface UsageRepository : JpaRepository<Usage, String> {
}