package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import com.avivba.simbanyu.api.features.usage.model.Usage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.*

interface UsageRepository : JpaRepository<Usage, UUID>, JpaSpecificationExecutor<Usage> {

    @Modifying
    @Query("UPDATE Usage u SET u.createdAt = :date, u.updatedAt = :date WHERE u.id = :id")
    fun updateTimestamps(id: UUID, date: Instant)

    fun findFirstBySubscriberOrderByCreatedAtDesc(subscriber: Subscriber): Usage?
}