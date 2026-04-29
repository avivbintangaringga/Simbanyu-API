package com.avivba.simbanyu.api.features.bill

import com.avivba.simbanyu.api.config.PriceProperties
import com.avivba.simbanyu.api.features.notification.NotificationService
import com.avivba.simbanyu.api.features.plans.Plan
import com.avivba.simbanyu.api.features.plans.PlanRepository
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
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
    private val subscriberRepository: SubscriberRepository,
    private val usageRepository: UsageRepository,
    private val notificationService: NotificationService,
    private val planRepository: PlanRepository,
    private val priceProperties: PriceProperties
) {
    @Transactional
    fun generateBill(
        subscriberId: String,
        usageId: String,
    ): Bill {
        val subscriber = subscriberRepository.findById(UUID.fromString(subscriberId)).orElseThrow()
        val usage = usageRepository.findById(UUID.fromString(usageId)).orElseThrow()

        val plan = subscriber.plan
        val tieredCost = calculateTieredCost(plan, usage.amount)

        val items = listOf(
            BillItem(
                name = "Biaya tetap",
                amount = plan.basePrice
            ),
            BillItem(
                name = "Biaya penggunaan (${usage.amount} m³)",
                amount = tieredCost
            ),
            BillItem(
                name = "Biaya admin",
                amount = priceProperties.admin
            )
        )

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

    fun calculateTieredCost(
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