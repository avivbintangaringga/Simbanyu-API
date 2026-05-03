package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.usage.model.UsageDto
import com.avivba.simbanyu.api.features.usage.model.UsageRequest
import com.avivba.simbanyu.api.features.user.UserRepository
import com.avivba.simbanyu.api.utils.hasRole
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/usages")
class UsageController(
    private val usageService: UsageService,
    private val userRepository: UserRepository,
    private val subscriberRepository: SubscriberRepository,
) {
    @GetMapping
    fun getSubscriberUsageWithLimit(
        @RequestParam("subscriberId") subscriberId: String?,
        @RequestParam("limit") limit: Int?,
        authentication: Authentication,
    ): ApiResponse<List<UsageDto>> {
        val isAdmin = authentication.hasRole("ADMIN")
        val isOperator = authentication.hasRole("OPERATOR")

        return (if (isAdmin || isOperator) {
            if (subscriberId == null) {
                usageService.getAllUsage()
            } else {
                usageService.getUsageBySubscriberId(subscriberId)
            }
        } else {
            val subscriber = subscriberRepository.findSubscriberByUser_Username(authentication.name)
            if (subscriber.isEmpty()) {
                throw Exception("User is not a subscriber")
            }

            val subscriberId = subscriber[0].id.toString()
            usageService.getUsageBySubscriberId(
                subscriberId,
                limit
            )
        })
            .map { UsageDto.fromEntity(it) }
            .let { ApiResponse.success(it) }
    }

    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    fun recordUsage(
        @Valid @RequestPart("data") data: UsageRequest,
        @RequestPart("image") image: MultipartFile,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ApiResponse<UsageDto> {
        val user = userRepository.findByUsername(userDetails.username)
        return usageService.recordUsage(
            data = data,
            image = image,
            user = user
        ).let {
            ApiResponse.success(UsageDto.fromEntity(it))
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    fun updateRecord(
        @PathVariable id: String,
        @Valid @RequestPart("data") data: UsageRequest,
        @RequestPart("image") image: MultipartFile?,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ApiResponse<UsageDto> {
        val user = userRepository.findByUsername(userDetails.username)
        return usageService.updateUsage(
            id = id,
            data = data,
            image = image,
            user = user).let {
            ApiResponse.success(UsageDto.fromEntity(it))
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteRecord(
        @PathVariable id: String,
    ): ApiResponse<Unit> {
        return usageService.deleteUsage(id).let {
            ApiResponse.success(Unit)
        }
    }
}