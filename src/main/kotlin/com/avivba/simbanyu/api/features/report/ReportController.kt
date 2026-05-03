package com.avivba.simbanyu.api.features.report

import com.avivba.simbanyu.api.base.ApiResponse
import com.avivba.simbanyu.api.features.report.model.AddReportRequest
import com.avivba.simbanyu.api.features.report.model.HandlingRequest
import com.avivba.simbanyu.api.features.report.model.ReportDto
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.user.UserRepository
import com.avivba.simbanyu.api.utils.hasRole
import jakarta.validation.Valid
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
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/reports")
class ReportController(
    private val reportService: ReportService,
    private val subscriberRepository: SubscriberRepository,
) {
    @GetMapping
    fun getReports(
        authentication: Authentication
    ): ApiResponse<List<ReportDto>> {
        val isAdmin = authentication.hasRole("ADMIN")
        val isOperator = authentication.hasRole("OPERATOR")

        return (if (isAdmin || isOperator) {
            reportService.getAllReports()
        } else  {
            val subscriber = subscriberRepository.findSubscriberByUser_Username(authentication.name)
            if (subscriber.isNotEmpty()) {
                reportService.getReportBySubscriberId(subscriberId = subscriber[0].id.toString())
            } else {
                throw Exception("User is not a subscriber")
            }
        }).let {
            ApiResponse.success(
                it.map {
                    ReportDto.fromEntity(it)
                })
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('SUBSCRIBER')")
    fun createReport(
        @AuthenticationPrincipal userDetails: UserDetails,
        @Valid @RequestPart("data") data: AddReportRequest,
        @RequestPart("image") image: MultipartFile
    ): ApiResponse<ReportDto> {
        val subscriber = subscriberRepository.findSubscriberByUser_Username(userDetails.username)
        if (subscriber.isEmpty()) {
            throw Exception("User is not a subscriber")
        }

        return reportService.addReport(
            subscriberId = subscriber[0].id.toString(),
            data = data,
            image = image
        ).let {
            ApiResponse.success(ReportDto.fromEntity(it))
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    fun handleReport(
        @PathVariable id: String,
        @Valid @RequestPart("data") data: HandlingRequest,
        @RequestPart("image") image: MultipartFile
    ): ApiResponse<ReportDto> {
        return reportService.handleReport(
            id = id,
            data = data,
            image = image
        ).let {
            ApiResponse.success(ReportDto.fromEntity(it))
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteReport(
        @PathVariable id: String
    ): ApiResponse<Unit> {
        reportService.deleteReport(id)
        return ApiResponse.success(Unit)
    }
}