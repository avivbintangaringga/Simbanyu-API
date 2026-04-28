package com.avivba.simbanyu.api.features.subscriber

import org.springframework.data.jpa.repository.JpaRepository

interface SubscriberRepository : JpaRepository<Subscriber, String> {
    fun findSubscriberByMeterId(meterId: String): Subscriber?
}