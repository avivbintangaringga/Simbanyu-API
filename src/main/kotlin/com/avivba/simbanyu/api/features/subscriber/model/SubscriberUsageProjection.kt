package com.avivba.simbanyu.api.features.subscriber.model

import com.avivba.simbanyu.api.features.usage.model.Usage

interface SubscriberUsageProjection {
    val subscriber: Subscriber
    val lastUsage: Usage?
}