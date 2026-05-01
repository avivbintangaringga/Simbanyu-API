package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.features.user.UserRepository
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/usages")
class UsageController(
    private val usageService: UsageService,
    private val userRepository: UserRepository,
) {
    @GetMapping
    fun getSubscriberUsageWithLimit(
        @RequestParam("subscriberId") subscriberId: String?,
        @RequestParam("limit") limit: Int?,
    ): ApiResponse<List<UsageDto>> {
        return if (subscriberId == null) {
            usageService.getAllUsage()
                .map { UsageDto.fromEntity(it) }
                .let {
                    ApiResponse.success(it)
                }
        } else {
            usageService.getUsageBySubscriberId(
                subscriberId,
                limit
            ).map { UsageDto.fromEntity(it) }
                .let {
                    ApiResponse.success(it)
                }
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    fun recordUsage(
        @Valid @RequestBody data: UsageRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ApiResponse<UsageDto> {
        val user = userRepository.findByUsername(userDetails.username)
        return usageService.recordUsage(data, user).let {
            ApiResponse.success(UsageDto.fromEntity(it))
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    fun updateRecord(
        @PathVariable id: String,
        @Valid @RequestBody data: UsageRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ApiResponse<UsageDto> {
        val user = userRepository.findByUsername(userDetails.username)
        return usageService.updateUsage(id, data, user).let {
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