package com.avivba.simbanyu.api.features.bill

import com.avivba.simbanyu.api.config.properties.PriceProperties
import com.avivba.simbanyu.api.features.bill.model.Bill
import com.avivba.simbanyu.api.features.bill.model.BillItem
import com.avivba.simbanyu.api.features.bill.model.BillStatus
import com.avivba.simbanyu.api.features.notification.NotificationService
import com.avivba.simbanyu.api.features.plans.model.Plan
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import com.avivba.simbanyu.api.features.usage.model.Usage
import com.avivba.simbanyu.api.features.user.UserRepository
import com.avivba.simbanyu.api.features.user.model.UserRole
import com.avivba.simbanyu.api.utils.getMonthName
import com.avivba.simbanyu.api.utils.getYear
import com.avivba.simbanyu.api.utils.toDottedCurrency
import jakarta.transaction.Transactional
import org.springframework.data.domain.Limit
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.util.*

@Service
class BillService(
    private val userRepository: UserRepository,
    private val subscriberRepository: SubscriberRepository,
    private val billRepository: BillRepository,
    private val billItemRepository: BillItemRepository,
    private val notificationService: NotificationService,
    private val priceProperties: PriceProperties
) {
    fun getAll(
        username: String,
        subscriberId: String?,
        limit: Int?,
        activeOnly: Boolean?,
    ): List<Bill> {
        val user = userRepository.findByUsername(username)
        var subscriberId = subscriberId

        if (user.role == UserRole.SUBSCRIBER) {
            val subscriber = subscriberRepository.findSubscriberByUser_Username(username).firstOrNull()
                ?: throw IllegalArgumentException("User is not a subscriber")

            if (subscriberId != null && subscriberId != subscriber.id.toString()) {
                throw AccessDeniedException("Cannot access other subscriber's data")
            }

            subscriberId = subscriber.id.toString()
        }

        val sort = Sort.by(Sort.Direction.DESC, "createdAt")

        val page = limit?.let {
            PageRequest.of(0, limit, sort)
        } ?: Pageable.unpaged(sort)

        val specs = BillSpecs.filterBy(
            subscriberId = subscriberId,
            activeOnly = activeOnly,
        )

        return billRepository.findAll(specs, page).content
    }

    //    fun getAllBills(): List<Bill> {
//        return billRepository.findAll()
//    }
//
    fun getBillById(id: String): Bill {
        return billRepository.findById(UUID.fromString(id)).orElseThrow()
    }

//    fun getBillsBySubscriberId(
//        subscriberId: String,
//        activeOnly: Boolean = false,
//    ): List<Bill> {
//        return if (activeOnly) {
//            billRepository.findBillsBySubscriber_IdAndStatus(UUID.fromString(subscriberId), BillStatus.UNPAID)
//        } else {
//            billRepository.findBillsBySubscriber_Id(UUID.fromString(subscriberId))
//        }
//    }

    @Transactional
    fun generateBill(
        subscriber: Subscriber,
        usage: Usage,
    ): Bill {
        val plan = subscriber.plan
        val usageAmount = usage.meterEnd - usage.meterStart

        val billItems = createBillItems(plan, usageAmount)
        val totalCost = billItems.sumOf { it.amount }

        val bill = Bill(
            amount = totalCost,
            status = BillStatus.UNPAID,
            subscriber = subscriber,
            usage = usage,
        )

        val billResult = billRepository.save(bill)
        billItemRepository.saveAll(billItems.map {
            it.bill = billResult
            it
        })

        notificationService.sendNotificationToUser(
            title = "Tagihan Bulan ${usage.createdAt.getMonthName()} ${usage.createdAt.getYear()}",
            body = "Anda memiliki tagihan sebesar ${billResult.amount.toDottedCurrency()}",
            user = subscriber.user
        )

        return billResult
    }

    @Transactional
    fun updateBillWithUsage(
        bill: Bill,
        usage: Usage,
    ): Bill {
        val plan = bill.subscriber.plan
        val usageAmount = usage.meterEnd - usage.meterStart

        bill.billItems.clear()
        val items = createBillItems(plan, usageAmount, bill)
        bill.billItems.addAll(items)

        val totalCost = bill.billItems.sumOf { it.amount }

        bill.amount = totalCost
        bill.usage = usage

        return billRepository.save(bill)
    }

    fun createBillItems(
        plan: Plan,
        usageAmount: Int,
        bill: Bill? = null
    ) = listOf(
        BillItem(
            name = "Biaya tetap",
            amount = plan.basePrice,
            bill = bill
        ),
        BillItem(
            name = "Biaya penggunaan (${usageAmount} m³)",
            amount = calculateTieredCost(plan, usageAmount),
            bill = bill
        ),
        BillItem(
            name = "Biaya admin",
            amount = priceProperties.admin,
            bill = bill
        )
    )

    private fun calculateTieredCost(
        plan: Plan,
        usageAmount: Int,
    ): Double {
        var remainingUsage = usageAmount
        var totalCost = 0.0

        for (price in plan.prices) {
            if (remainingUsage <= 0) {
                break
            }

            val tierUsage = if (price.maxVolume != null) {
                minOf(remainingUsage, price.maxVolume!!)
            } else {
                remainingUsage
            }

            val tierCost = tierUsage * price.pricePerMeter

            totalCost += tierCost
            remainingUsage -= tierUsage
        }

        return totalCost
    }
}