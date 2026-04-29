package com.avivba.simbanyu.api.features.usage

import com.avivba.simbanyu.api.base.ApiResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/usages")
class UsageController(
    val usageService: UsageService,
) {
    @GetMapping
    fun getAllUsage(): ApiResponse<List<Usage>> {
        return ApiResponse.success(usageService.getAllUsage())
    }

    @GetMapping
    fun getSubscriberUsage(
        @RequestParam("subscriberId") subscriberId: String,
    ): ApiResponse<List<Usage>> {
        usageService.getUsageBySubscriberId(
            subscriberId
        ).let {
            return ApiResponse.success(it)
        }
    }

    @GetMapping
    fun getSubscriberUsageWithLimit(
        @RequestParam("subscriberId") subscriberId: String,
        @RequestParam("limit") limit: Int,
    ): ApiResponse<List<Usage>> {
        usageService.getUsageBySubscriberId(
            subscriberId,
            limit
        ).let { return ApiResponse.success(it) }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    fun recordUsage(
        @RequestBody data: RecordUsageRequest
    ): ApiResponse<Usage> {
        val usage = usageService.recordUsage(data)
        return ApiResponse.success(usage)
    }
}