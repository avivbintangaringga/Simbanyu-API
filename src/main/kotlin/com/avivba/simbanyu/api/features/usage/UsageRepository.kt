package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.features.subscriber.Subscriber
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface UsageRepository : JpaRepository<Usage, UUID> {
    @Query("SELECT u FROM Usage u WHERE u.subscriber.id = :id ORDER BY u.createdAt DESC LIMIT :limit")
    fun getUsageBySubscriberIdWithLimit(id: UUID, limit: Int): List<Usage>

    fun findUsagesBySubscriberIdOrderByCreatedAtDesc(subscriberId: UUID): List<Usage>
}