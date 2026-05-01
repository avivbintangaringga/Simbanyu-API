package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.features.activity.ActivityService
import com.avivba.simbanyu.api.features.bill.BillService
import com.avivba.simbanyu.api.features.notification.NotificationService
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.user.User
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UsageService(
    private val subscriberRepository: SubscriberRepository,
    private val usageRepository: UsageRepository,
    private val billService: BillService,
    private val notificationService: NotificationService,
    private val activityService: ActivityService,
) {
    fun getAllUsage(): List<Usage> = usageRepository.findAll()
    fun getUsageBySubscriberId(
        subscriberId: String,
        limit: Int? = null,
    ): List<Usage> {
        return if (limit != null) {
            usageRepository.getUsageBySubscriberIdWithLimit(
                UUID.fromString(subscriberId),
                limit
            )
        } else {
            usageRepository.findUsagesBySubscriberIdAndIsInitialOrderByCreatedAtDesc(
                UUID.fromString(subscriberId),
                isInitial = false
            )
        }
    }

    fun addInitialUsage(
        subscriberId: String,
        currentMeter: Int,
    ): Usage {
        val subscriber = subscriberRepository.findById(UUID.fromString(subscriberId)).orElseThrow()

        val usage = Usage(
            meterStart = currentMeter,
            meterEnd = currentMeter,
            isInitial = true,
            evidenceImageUrl = "https://example.com/image.jpg",
            operatorNotes = null,
            subscriber = subscriber,
            bill = null,
        )

        return usageRepository.save(usage)
    }

    @Transactional
    fun recordUsage(
        data: UsageRequest,
        user: User
    ): Usage {
        val subscriber = subscriberRepository.findById(UUID.fromString(data.subscriberId)).orElseThrow()
        val previousUsage = usageRepository.findFirstBySubscriberOrderByCreatedAtDesc(subscriber)
            ?: throw IllegalStateException("Subscriber doesn't have any previous usage record")

        if (data.currentMeter <= previousUsage.meterEnd) {
            throw IllegalStateException("Current meter must be greater than previous meter")
        }

        // TODO: Check if current month is already recorded
        // TODO: Store image and get URL
        val imageUrl = ""

        val usage = Usage(
            meterStart = previousUsage.meterEnd,
            meterEnd = data.currentMeter,
            evidenceImageUrl = imageUrl,
            operatorNotes = data.operatorNotes,
            subscriber = subscriber
        )

        val result = usageRepository.save(usage)

        val bill = billService.generateBill(
            subscriber = subscriber,
            usage = result
        )

        result.bill = bill
        val finalResult = usageRepository.save(result)

        notificationService.sendNotificationToUser(
            title = "Pencatatan Meter (${subscriber.meterId})",
            body = "Pencatatan meter berhasil dilakukan.",
            user = user
        )

        activityService.recordActivity(
            title = "Pencatatan Meter (${subscriber.meterId})",
            body = "Pencatatan meter berhasil dilakukan.",
            user = user,
        )

        return finalResult
    }

    @Transactional
    fun updateUsage(
        id: String,
        data: UsageRequest,
        user: User,
    ): Usage {
        val usage = usageRepository.findById(UUID.fromString(id)).orElseThrow()

        if (data.currentMeter <= usage.meterStart) {
            throw IllegalStateException("Current meter must be greater than previous meter")
        }

        // TODO: Store image and get URL
        val imageUrl = ""

        usage.meterEnd = data.currentMeter
        usage.evidenceImageUrl = imageUrl
        usage.operatorNotes = data.operatorNotes

        val bill = usage.bill ?: throw IllegalStateException("Usage doesn't have a bill associated")

        val updatedBill = billService.updateBillWithUsage(bill, usage)
        usage.bill = updatedBill

        val updatedUsage = usageRepository.save(usage)

        notificationService.sendNotificationToUser(
            title = "Pembaruan Pencatatan Meter (${usage.subscriber.meterId})",
            body = "Pembaruan pencatatan meter berhasil dilakukan.",
            user = user
        )

        activityService.recordActivity(
            title = "Pembaruan pencatatan Meter (${usage.subscriber.meterId})",
            body = "Pembaruan pencatatan meter berhasil dilakukan.",
            user = user,
        )

        return updatedUsage
    }

    fun deleteUsage(id: String) {
        usageRepository.deleteById(UUID.fromString(id))
    }
}