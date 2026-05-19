package com.avivba.simbanyu.api.features.subscriber

import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import com.avivba.simbanyu.api.features.subscriber.model.SubscriberUsageProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface SubscriberRepository : JpaRepository<Subscriber, UUID> {
    fun findSubscriberByMeterId(meterId: String): Subscriber?
    fun findSubscriberByUser_Username(userUsername: String): MutableList<Subscriber>

    // Get ALL subscribers with their latest usage
    @Query("""
        SELECT s AS subscriber, u AS lastUsage 
        FROM Subscriber s 
        LEFT JOIN Usage u ON u.subscriber = s 
        WHERE u.createdAt = (SELECT MAX(u2.createdAt) FROM Usage u2 WHERE u2.subscriber = s) 
           OR u.createdAt IS NULL
    """)
    fun findAllWithLastUsage(): List<SubscriberUsageProjection>

    // Get specific subscriber by ID with their latest usage
    @Query("""
        SELECT s AS subscriber, u AS lastUsage 
        FROM Subscriber s 
        LEFT JOIN Usage u ON u.subscriber = s 
        WHERE s.id = :id 
        AND (u.createdAt = (SELECT MAX(u2.createdAt) FROM Usage u2 WHERE u2.subscriber = s) 
             OR u.createdAt IS NULL)
    """)
    fun findWithLastUsageById(id: UUID): SubscriberUsageProjection?
}