package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.features.activity.ActivityService
import com.avivba.simbanyu.api.features.bill.BillService
import com.avivba.simbanyu.api.features.upload.UploadService
import com.avivba.simbanyu.api.features.notification.NotificationService
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.usage.model.Usage
import com.avivba.simbanyu.api.features.usage.model.UsageRequest
import com.avivba.simbanyu.api.features.user.UserRepository
import com.avivba.simbanyu.api.features.user.model.User
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class UsageService(
    private val subscriberRepository: SubscriberRepository,
    private val usageRepository: UsageRepository,
    private val billService: BillService,
    private val notificationService: NotificationService,
    private val activityService: ActivityService,
    private val uploadService: UploadService,
    private val userRepository: UserRepository,
) {
    fun getAllUsage(): List<Usage> {
        return usageRepository.findUsagesByIsInitialOrderByCreatedAtDesc(isInitial = false)
    }

    fun getUsageBySubscriberId(
        subscriberId: String,
        limit: Int? = null,
    ): List<Usage> {
        return if (limit != null) {
            usageRepository.findUsageBySubscriberIdAndIsInitialOrderByCreatedAtDescWithLimit(
                id = UUID.fromString(subscriberId),
                isInitial = false,
                limit = limit
            )
        } else {
            usageRepository.findUsagesBySubscriberIdAndIsInitialOrderByCreatedAtDesc(
                subscriberId = UUID.fromString(subscriberId),
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
            evidenceImage = null,
            operatorNotes = null,
            subscriber = subscriber,
            bill = null,
        )

        return usageRepository.save(usage)
    }

    @Transactional
    fun recordUsage(
        data: UsageRequest,
        image: MultipartFile,
        username: String,
    ): Usage {
        val currentUser = userRepository.findByUsername(username)
        val subscriber = subscriberRepository.findById(UUID.fromString(data.subscriberId)).orElseThrow()
        val previousUsage = usageRepository.findFirstBySubscriberOrderByCreatedAtDesc(subscriber)
            ?: throw IllegalStateException("Subscriber doesn't have any previous usage record")

        if (data.currentMeter <= previousUsage.meterEnd) {
            throw IllegalArgumentException("Current meter must be greater than previous meter")
        }

        // TODO: Check if current month is already recorded

        val upload = uploadService.saveMultipart(image)

        val usage = Usage(
            meterStart = previousUsage.meterEnd,
            meterEnd = data.currentMeter,
            evidenceImage = upload,
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
            user = currentUser
        )

        activityService.recordActivity(
            title = "Pencatatan Meter (${subscriber.meterId})",
            body = "Pencatatan meter berhasil dilakukan.",
            user = currentUser
        )

        return finalResult
    }

    @Transactional
    fun updateUsage(
        id: String,
        data: UsageRequest,
        image: MultipartFile?,
        username: String
    ): Usage {
        val currentUser = userRepository.findByUsername(username)
        val usage = usageRepository.findById(UUID.fromString(id)).orElseThrow()

        if (data.currentMeter <= usage.meterStart) {
            throw IllegalArgumentException("Current meter must be greater than previous meter")
        }

        if (image != null) {
            if (usage.evidenceImage != null) {
                uploadService.deleteFile(usage.evidenceImage!!)
            }
            usage.evidenceImage = uploadService.saveMultipart(image)
        }

        usage.meterEnd = data.currentMeter
        usage.operatorNotes = data.operatorNotes

        val bill = usage.bill ?: throw IllegalStateException("Usage doesn't have a bill associated")

        val updatedBill = billService.updateBillWithUsage(bill, usage)
        usage.bill = updatedBill

        val updatedUsage = usageRepository.save(usage)

        notificationService.sendNotificationToUser(
            title = "Pembaruan Pencatatan Meter (${usage.subscriber.meterId})",
            body = "Pembaruan pencatatan meter berhasil dilakukan.",
            user =  currentUser
        )

        activityService.recordActivity(
            title = "Pembaruan pencatatan Meter (${usage.subscriber.meterId})",
            body = "Pembaruan pencatatan meter berhasil dilakukan.",
            user = currentUser
        )

        return updatedUsage
    }

    fun deleteUsage(id: String, username: String) {
        val user = userRepository.findByUsername(username)
        val usage = usageRepository.findById(UUID.fromString(id)).orElseThrow()
        usageRepository.delete(usage)
        activityService.recordActivity(
            user = user,
            title = "Hapus Pencatatan Meter",
            body = "Pencatatan meter ${id} telah dihapus.",
        )
    }
}