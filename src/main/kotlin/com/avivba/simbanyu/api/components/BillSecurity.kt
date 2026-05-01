package com.avivba.simbanyu.api.components

import com.avivba.simbanyu.api.features.bill.BillRepository
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.user.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.UUID

@Component("billSecurity")
class BillSecurity(
    private val billRepository: BillRepository,
    private val subscriberRepository: SubscriberRepository,
) {
    fun isOwnBill(billId: String, authentication: Authentication): Boolean {
        val subscriber = subscriberRepository.findSubscriberByUser_Username(authentication.name)
        val bill = billRepository.findById(UUID.fromString(billId)).orElseThrow()
        return bill.subscriber.id == subscriber[0].id
    }
}