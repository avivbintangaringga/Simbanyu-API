package com.avivba.simbanyu.api.features.subscriber

import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface SubscriberRepository : JpaRepository<Subscriber, UUID> {
    fun findSubscriberByMeterId(meterId: String): Subscriber?

    @Query("SELECT s, (SELECT MAX(u.createdAt) FROM Usage u WHERE u.subscriber = s) FROM Subscriber s")
    fun findAllWithLastUsageDate(): List<Array<Any?>>

    @Query("SELECT s, (SELECT MAX(u.createdAt) FROM Usage u WHERE u.subscriber = s) FROM Subscriber s WHERE s.id = :id")
    fun findByIdWithLastUsageDate(id: UUID): Array<Any?>
    fun findSubscriberByUser_Username(userUsername: String): MutableList<Subscriber>
}