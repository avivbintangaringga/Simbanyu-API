package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import com.avivba.simbanyu.api.features.usage.model.Usage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UsageRepository : JpaRepository<Usage, UUID>, JpaSpecificationExecutor<Usage> {
//    @Query("SELECT u FROM Usage u WHERE (u.subscriber.id = :id AND u.isInitial = :isInitial) ORDER BY u.createdAt DESC LIMIT :limit")
//    fun findUsageBySubscriberIdAndIsInitialOrderByCreatedAtDescWithLimit(id: UUID, isInitial: Boolean, limit: Int): List<Usage>
//
//    fun findUsagesBySubscriberIdAndIsInitialOrderByCreatedAtDesc(
//        subscriberId: UUID,
//        isInitial: Boolean = false
//    ): List<Usage>

    fun findFirstBySubscriberOrderByCreatedAtDesc(subscriber: Subscriber): Usage?
//    fun findUsagesByIsInitialOrderByCreatedAtDesc(isInitial: Boolean): MutableList<Usage>
}