package com.avivba.simbanyu.api.features.subscriber

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SubscriberRepository : JpaRepository<Subscriber, UUID> {
    fun findSubscriberByMeterId(meterId: String): Subscriber?
}