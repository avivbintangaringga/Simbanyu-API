package com.avivba.simbanyu.api.features.bill

import com.avivba.simbanyu.api.config.PriceProperties
import com.avivba.simbanyu.api.features.notification.NotificationService
import com.avivba.simbanyu.api.features.plans.Plan
import com.avivba.simbanyu.api.features.plans.PlanRepository
import com.avivba.simbanyu.api.features.subscriber.Subscriber
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.usage.Usage
import com.avivba.simbanyu.api.features.usage.UsageRepository
import com.avivba.simbanyu.api.features.usage.UsageService
import com.avivba.simbanyu.api.utils.getMonthName
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BillService(
    private val billRepository: BillRepository,
    private val billItemRepository: BillItemRepository,
    private val notificationService: NotificationService,
    private val priceProperties: PriceProperties
) {
    @Transactional
    fun generateBill(
        subscriber: Subscriber,
        usage: Usage,
    ): Bill {
        val plan = subscriber.plan
        val usageAmount = usage.meterEnd - usage.meterStart
        val tieredCost = calculateTieredCost(plan, usageAmount)

        val items = createBillItems(plan, usageAmount, tieredCost)
        val billItems = billItemRepository.saveAll(items)
        val totalCost = billItems.sumOf { it.amount }

        val bill = Bill(
            amount = totalCost,
            status = BillStatus.UNPAID,
            subscriber = subscriber,
            usage = usage,
            billItems = billItems,
        )

        val billResult = billRepository.save(bill)

        notificationService.sendNotificationToUser(
            title = "Tagihan Bulan ${usage.createdAt.getMonthName()}",
            body = "Tagihan anda sebesar Rp${billResult.amount} telah dikeluarkan.",
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
        val tieredCost = calculateTieredCost(plan, usageAmount)

        billItemRepository.deleteAll(bill.billItems)

        val items = createBillItems(plan, usageAmount, tieredCost)
        val billItems = billItemRepository.saveAll(items)
        val totalCost = billItems.sumOf { it.amount }

        // Update
        bill.amount = totalCost
        bill.billItems = billItems
        bill.usage = usage

        return billRepository.save(bill)
    }

    private fun createBillItems(
        plan: Plan,
        usageAmount: Int,
        tieredCost: Double,
    ) = listOf(
        BillItem(
            name = "Biaya tetap",
            amount = plan.basePrice
        ),
        BillItem(
            name = "Biaya penggunaan (${usageAmount} m³)",
            amount = tieredCost
        ),
        BillItem(
            name = "Biaya admin",
            amount = priceProperties.admin
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