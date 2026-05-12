package com.avivba.simbanyu.api.features.notification

import com.avivba.simbanyu.api.features.notification.model.Notification
import com.avivba.simbanyu.api.features.user.model.User
import org.springframework.data.domain.Limit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface NotificationRepository : JpaRepository<Notification, UUID> {
    fun findNotificationsByUser(user: User, limit: Limit): MutableList<Notification>

    @Modifying
    @Query("UPDATE Notification n SET n.createdAt = :date, n.updatedAt = :date WHERE n.id = :id")
    fun updateTimestamps(id: UUID, date: Instant)

    @Modifying
    @Query("UPDATE Notification n SET n.createdAt = :date, n.updatedAt = :date WHERE n.id IN :ids")
    fun updateTimestampsInBatch(ids: List<UUID>, date: Instant)
}