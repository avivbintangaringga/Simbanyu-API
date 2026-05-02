package com.avivba.simbanyu.api.features.subscriber

import com.avivba.simbanyu.api.features.plans.PlanRepository
import com.avivba.simbanyu.api.features.subscriber.model.AddSubscriberRequest
import com.avivba.simbanyu.api.features.subscriber.model.Subscriber
import com.avivba.simbanyu.api.features.subscriber.model.SubscriberDto
import com.avivba.simbanyu.api.features.subscriber.model.UpdateSubscriberRequest
import com.avivba.simbanyu.api.features.usage.UsageService
import com.avivba.simbanyu.api.features.user.model.User
import com.avivba.simbanyu.api.features.user.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.UUID

@Service
class SubscriberService(
    private val subscriberRepository: SubscriberRepository,
    private val userRepository: UserRepository,
    private val planRepository: PlanRepository,
    private val usageService: UsageService,
    private val encoder: PasswordEncoder,
) {
    fun getAllSubscribers(): List<SubscriberDto> {
        val results = subscriberRepository.findAllWithLastUsageDate()
        val currentMonth = YearMonth.now(ZoneId.systemDefault())

        return results.map { rowToDto(it, currentMonth) }
    }

    fun getSubscriberById(id: String): SubscriberDto {
        val row = subscriberRepository.findByIdWithLastUsageDate(UUID.fromString(id))
        // TODO: Find a better way to get the current month
        val currentMonth = YearMonth.now(ZoneId.systemDefault())
        return rowToDto(row, currentMonth)
    }

    @Transactional
    fun addSubscriber(
        data: AddSubscriberRequest,
    ): Subscriber {
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

        return savedSubscriber
    }

    fun updateSubscriber(
        id: String,
        data: UpdateSubscriberRequest
    ): Subscriber {
        val subscriber = subscriberRepository.findById(UUID.fromString(id)).orElseThrow()

        subscriber.meterId = data.meterId
        subscriber.address = data.address
        subscriber.longitude = data.longitude
        subscriber.latitude = data.latitude
        subscriber.plan = planRepository.findById(UUID.fromString(data.planId)).orElseThrow()

        return subscriberRepository.save(subscriber)
    }

    fun deleteSubscriberById(id: String) {
        val subscriber = subscriberRepository.findById(UUID.fromString(id)).orElseThrow()
        userRepository.delete(subscriber.user)
        subscriberRepository.delete(subscriber)
    }

    private fun rowToDto(row: Array<Any?>, currentMonth: YearMonth): SubscriberDto {
        val subscriber = row[0] as Subscriber
        val lastRecordedAt = row[1] as Instant?
        val isRecorded = lastRecordedAt?.let {
            currentMonth == YearMonth.from(it.atZone(ZoneId.systemDefault()))
        } ?: false

        return SubscriberDto.fromEntity(
            subscriber,
            isRecorded,
            lastRecordedAt?.let { LocalDate.ofInstant(it, ZoneId.systemDefault()).toString() }
        )
    }
}