package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.features.activity.ActivityService
import com.avivba.simbanyu.api.features.bill.BillService
import com.avivba.simbanyu.api.features.bill.BillSpecs
import com.avivba.simbanyu.api.features.notification.NotificationService
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.upload.UploadService
import com.avivba.simbanyu.api.features.usage.model.Usage
import com.avivba.simbanyu.api.features.usage.model.UsageRequest
import com.avivba.simbanyu.api.features.usage.model.UsageUpdateRequest
import com.avivba.simbanyu.api.features.user.UserRepository
import com.avivba.simbanyu.api.features.user.model.UserRole
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.util.*

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
    fun getAll(
        username: String,
        subscriberId: String?,
        limit: Int?,
        includeInitial: Boolean?,
    ): List<Usage> {
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

        val specs = UsageSpecs.filterBy(
            subscriberId = subscriberId,
            includeInitial = includeInitial ?: false
        )

        return usageRepository.findAll(specs, page).content
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

        val currentYearMonth = YearMonth.from(Instant.now().atZone(ZoneId.systemDefault()))
        val previousYearMonth = YearMonth.from(previousUsage.createdAt.atZone(ZoneId.systemDefault()))

        if (currentYearMonth == previousYearMonth && !previousUsage.isInitial) {
            throw IllegalArgumentException("Current month is already recorded")
        }

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
        data: UsageUpdateRequest,
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
            user = currentUser
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