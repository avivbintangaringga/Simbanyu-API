package com.avivba.simbanyu.api.features.subscriber

import com.avivba.simbanyu.api.features.user.UserRepository
import org.springframework.stereotype.Service

@Service
class SubscriberService(
    private val subscriberRepository: SubscriberRepository,
    private val userRepository: UserRepository,
) {
    // TOOD: Implement usages first to get subscriber's meter
}