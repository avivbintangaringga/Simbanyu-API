package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.features.bill.BillService
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UsageService(
    private val subscriberRepository: SubscriberRepository,
    private val usageRepository: UsageRepository,
    private val billService: BillService,
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
            usageRepository.findUsagesBySubscriberIdOrderByCreatedAtDesc(
                UUID.fromString(subscriberId)
            )
        }
    }

    @Transactional
    fun recordUsage(
        data: RecordUsageRequest
    ): Usage {
        val subscriber = subscriberRepository.findById(UUID.fromString(data.subscriberId)).orElseThrow()

        val usage = Usage(
            meterStart = data.meterStart,
            amount = data.amount,
            evidenceImageUrl = data.evidenceImageURL,
            operatorNotes = data.operatorNotes,
            subscriber = subscriber
        )

        val result = usageRepository.save(usage)

        // TODO: Add notification
        // TODO: Add activity record

        billService.generateBill(
            subscriberId = data.subscriberId,
            usageId = result.id.toString()
        )

        return result
    }
}