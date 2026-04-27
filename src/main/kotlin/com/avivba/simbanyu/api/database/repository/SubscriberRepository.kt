package com.avivba.simbanyu.api.database.repository

import com.avivba.simbanyu.api.database.entity.Subscriber
import org.springframework.data.jpa.repository.JpaRepository

interface SubscriberRepository : JpaRepository<Subscriber, String> {

}