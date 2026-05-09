package com.avivba.simbanyu.api.features.subscriber

import com.avivba.simbanyu.api.features.activity.ActivityService
import com.avivba.simbanyu.api.features.plans.PlanRepository
import com.avivba.simbanyu.api.features.subscriber.model.AddSubscriberRequest
import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import com.avivba.simbanyu.api.features.subscriber.model.SubscriberDto
import com.avivba.simbanyu.api.features.subscriber.model.SubscriberUsageProjection
import com.avivba.simbanyu.api.features.subscriber.model.UpdateSubscriberRequest
import com.avivba.simbanyu.api.features.usage.UsageService
import com.avivba.simbanyu.api.features.user.UserRepository
import com.avivba.simbanyu.api.features.user.model.User
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.YearMonth
import java.time.ZoneId
import java.util.*

@Service
class SubscriberService(
    private val subscriberRepository: SubscriberRepository,
    private val userRepository: UserRepository,
    private val planRepository: PlanRepository,
    private val usageService: UsageService,
    private val encoder: PasswordEncoder,
    private val activityService: ActivityService,
) {
    fun getAllSubscribers(): List<SubscriberDto> {
        val results = subscriberRepository.findAllWithLastUsage()
        val currentMonth = YearMonth.now(ZoneId.systemDefault())

        return results.map { rowToDto(it, currentMonth) }
    }

    fun getSubscriberById(id: String): SubscriberDto {
        val result = subscriberRepository.findWithLastUsageById(UUID.fromString(id))
            ?: throw EntityNotFoundException("Subscriber not found")

        val currentMonth = YearMonth.now(ZoneId.systemDefault())
        return rowToDto(result, currentMonth)
    }

    fun findByUsername(username: String): Subscriber? {
        return subscriberRepository.findSubscriberByUser_Username(username).firstOrNull()
    }

    @Transactional
    fun addSubscriber(
        data: AddSubscriberRequest,
        username: String,
    ): Subscriber {
        val currentUser = userRepository.findByUsername(username)
        val user = User(
            username = data.meterId,
            password = encoder.encode(data.password)!!,
            name = data.name,
            phoneNumber = data.phoneNumber,
        )

        val plan = planRepository.findById(UUID.fromString(data.planId)).orElseThrow()
        val savedUser = userRepository.save(user)
        val subscriber = Subscriber(
            meterId = data.meterId,
            address = data.address,
            longitude = data.longitude,
            latitude = data.latitude,
            plan = plan,
            user = savedUser,
        )

        val savedSubscriber = subscriberRepository.save(subscriber)

        usageService.addInitialUsage(
            subscriberId = savedSubscriber.id.toString(),
            data.initialMeter
        )

        activityService.recordActivity(
            user = currentUser,
            title = "Tambah Subscriber",
            body = "Subscriber baru telah ditambahkan.",
        )

        return savedSubscriber
    }

    fun updateSubscriber(
        id: String,
        data: UpdateSubscriberRequest,
        username: String,
    ): Subscriber {
        val user = userRepository.findByUsername(username)
        val subscriber = subscriberRepository.findById(UUID.fromString(id)).orElseThrow()

        subscriber.meterId = data.meterId
        subscriber.address = data.address
        subscriber.longitude = data.longitude
        subscriber.latitude = data.latitude
        subscriber.plan = planRepository.findById(UUID.fromString(data.planId)).orElseThrow()

        val updatedSubscriber = subscriberRepository.save(subscriber)

        activityService.recordActivity(
            user = user,
            title = "Update Subscriber",
            body = "Subscriber ${id} telah diperbarui.",
        )

        return updatedSubscriber
    }

    fun deleteSubscriberById(id: String, username: String) {
        val user = userRepository.findByUsername(username)
        val subscriber = subscriberRepository.findById(UUID.fromString(id)).orElseThrow()
        userRepository.delete(subscriber.user)
        subscriberRepository.delete(subscriber)
        activityService.recordActivity(
            user = user,
            title = "Hapus Subscriber",
            body = "Subscriber ${id} telah dihapus.",
        )
    }


    private fun rowToDto(row: SubscriberUsageProjection, currentYearMonth: YearMonth): SubscriberDto {
        val subscriber = row.subscriber
        val lastUsage = row.lastUsage

        val isRecorded = lastUsage?.let {
            currentYearMonth == YearMonth.from(
                it.createdAt.atZone(ZoneId.systemDefault())
            ) && !it.isInitial
        } ?: false
        return SubscriberDto.fromEntity(
            subscriber,
            isRecorded,
            lastUsage?.createdAt?.toString()
        )
    }
}