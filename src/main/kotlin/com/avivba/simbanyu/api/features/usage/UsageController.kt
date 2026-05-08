package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.usage.model.UsageDto
import com.avivba.simbanyu.api.features.usage.model.UsageRequest
import com.avivba.simbanyu.api.features.usage.model.UsageUpdateRequest
import com.avivba.simbanyu.api.utils.hasRole
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/usages")
class UsageController(
    private val usageService: UsageService,
    private val subscriberRepository: SubscriberRepository,
) {
    @GetMapping
    @ResponseMessage("Usage retrieved successfully.")
    fun getSubscriberUsageWithLimit(
        @RequestParam("subscriberId") subscriberId: String?,
        @RequestParam("limit") limit: Int?,
        authentication: Authentication,
    ): List<UsageDto> {
        val isAdmin = authentication.hasRole("ADMIN")
        val isOperator = authentication.hasRole("OPERATOR")

        // TODO: Maybe simplify this logic?

        return (if (isAdmin || isOperator) {
            if (subscriberId == null) {
                usageService.getAllUsage()
            } else {
                val subscriber = subscriberRepository.findSubscriberByUser_Username(authentication.name)
                if (subscriber.isEmpty()) {
                    throw EntityNotFoundException("User is not a subscriber")
                }
                usageService.getUsageBySubscriberId(
                    subscriberId,
                    limit
                )
            }
        } else {
            val subscriber = subscriberRepository.findSubscriberByUser_Username(authentication.name)
            if (subscriber.isEmpty()) {
                throw EntityNotFoundException("User is not a subscriber")
            }

            val subscriberId = subscriber[0].id.toString()
            usageService.getUsageBySubscriberId(
                subscriberId,
                limit
            )
        })
            .sortedByDescending { it.createdAt }
            .map { UsageDto.fromEntity(it) }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @ResponseMessage("Usage recorded successfully.")
    fun recordUsage(
        @Valid @RequestPart("data") data: UsageRequest,
        @RequestPart("image") image: MultipartFile,
        authentication: Authentication,
    ): UsageDto {
        return usageService.recordUsage(
            data = data,
            image = image,
            username = authentication.name
        ).let {
            UsageDto.fromEntity(it)
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @ResponseMessage("Usage updated successfully.")
    fun updateRecord(
        @PathVariable id: String,
        @Valid @RequestPart("data") data: UsageUpdateRequest,
        @RequestPart("image") image: MultipartFile?,
        authentication: Authentication
    ): UsageDto {
        return usageService.updateUsage(
            id = id,
            data = data,
            image = image,
            username = authentication.name
        ).let {
            UsageDto.fromEntity(it)
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Usage deleted successfully.")
    fun deleteRecord(
        @PathVariable id: String,
        authentication: Authentication
    ) {
        usageService.deleteUsage(
            id = id,
            username = authentication.name
        )
    }
}