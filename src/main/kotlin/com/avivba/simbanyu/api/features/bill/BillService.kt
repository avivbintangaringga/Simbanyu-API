package com.avivba.simbanyu.api.features.bill

import com.avivba.simbanyu.api.config.properties.PriceProperties
import com.avivba.simbanyu.api.features.bill.model.Bill
import com.avivba.simbanyu.api.features.bill.model.BillItem
import com.avivba.simbanyu.api.features.bill.model.BillStatus
import com.avivba.simbanyu.api.features.notification.NotificationService
import com.avivba.simbanyu.api.features.plans.model.Plan
import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import com.avivba.simbanyu.api.features.usage.model.Usage
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
    fun getAllBills(): List<Bill> {
        return billRepository.findAll()
    }

    fun getBillById(id: String): Bill {
        return billRepository.findById(UUID.fromString(id)).orElseThrow()
    }

    fun getBillsBySubscriberId(
        subscriberId: String
    ): List<Bill> {
        return billRepository.findBillsBySubscriber_Id(UUID.fromString(subscriberId))
    }

    @Transactional
    fun generateBill(
        subscriber: Subscriber,
        usage: Usage,
    ): Bill {
        val plan = subscriber.plan
        val usageAmount = usage.meterEnd - usage.meterStart
        val tieredCost = calculateTieredCost(plan, usageAmount)

        val billItems = createBillItems(plan, usageAmount, tieredCost)
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

        bill.billItems.clear()
        val items = createBillItems(plan, usageAmount, tieredCost, bill)
        bill.billItems.addAll(items)

        val totalCost = bill.billItems.sumOf { it.amount }

        bill.amount = totalCost
        bill.usage = usage

        return billRepository.save(bill)
    }

    fun updateBillStatus(
        bill: Bill,
        status: BillStatus,
    ): Bill {
        bill.status = status
        val updatedBill = billRepository.save(bill)
        val user = bill.subscriber.user

        when (status) {
            BillStatus.PAID -> {
                notificationService.sendNotificationToUser(
                    title = "Tagihan Selesai Dibayar",
                    body = "Tagihan bulan ${updatedBill.createdAt.getMonthName()} telah berhasil dibayar",
                    user = user
                )
            }

            BillStatus.UNPAID -> {
                notificationService.sendNotificationToUser(
                    title = "Pembayaran Bulan ${updatedBill.createdAt.getMonthName()}",
                    body = "Anda belum melakukan pembayaran, segera lakukan pembayaran",
                    user = user
                )
            }

            BillStatus.PENDING -> {
                notificationService.sendNotificationToUser(
                    title = "Pembayaran Bulan ${updatedBill.createdAt.getMonthName()}",
                    body = "Permintaan pembayaran telah dibuat, segera lakukan pembayaran",
                    user = user
                )
            }
        }

        return updatedBill
    }

    private fun createBillItems(
        plan: Plan,
        usageAmount: Int,
        tieredCost: Double,
        bill: Bill? = null
    ) = listOf(
        BillItem(
            name = "Biaya tetap",
            amount = plan.basePrice,
            bill = bill
        ),
        BillItem(
            name = "Biaya penggunaan (${usageAmount} m³)",
            amount = tieredCost,
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