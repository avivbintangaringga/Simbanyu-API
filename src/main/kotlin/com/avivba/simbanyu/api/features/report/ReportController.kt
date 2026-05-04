package com.avivba.simbanyu.api.features.report

import com.avivba.simbanyu.api.config.wrapper.ResponseMessage
import com.avivba.simbanyu.api.features.report.model.AddReportRequest
import com.avivba.simbanyu.api.features.report.model.HandlingRequest
import com.avivba.simbanyu.api.features.report.model.ReportDto
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.utils.hasRole
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/reports")
class ReportController(
    private val reportService: ReportService,
    private val subscriberRepository: SubscriberRepository,
) {
    @GetMapping
    @ResponseMessage("Reports retrieved successfully.")
    fun getReports(
        authentication: Authentication
    ): List<ReportDto> {
        val isAdmin = authentication.hasRole("ADMIN")
        val isOperator = authentication.hasRole("OPERATOR")

        return (if (isAdmin || isOperator) {
            reportService.getAllReports()
        } else {
            val subscriber = subscriberRepository.findSubscriberByUser_Username(authentication.name)
            if (subscriber.isNotEmpty()) {
                reportService.getReportBySubscriberId(subscriberId = subscriber[0].id.toString())
            } else {
                throw IllegalArgumentException("User is not a subscriber")
            }
        }).let {
            it.map {
                ReportDto.fromEntity(it)
            }
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('SUBSCRIBER')")
    @ResponseMessage("Report created successfully.")
    fun createReport(
        @Valid @RequestPart("data") data: AddReportRequest,
        @RequestPart("image") image: MultipartFile,
        authentication: Authentication,
    ): ReportDto {
        return reportService.addReport(
            data = data,
            image = image,
            username = authentication.name
        ).let {
            ReportDto.fromEntity(it)
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @ResponseMessage("Report handled successfully.")
    fun handleReport(
        @PathVariable id: String,
        @Valid @RequestPart("data") data: HandlingRequest,
        @RequestPart("image") image: MultipartFile,
        authentication: Authentication
    ): ReportDto {
        return reportService.handleReport(
            id = id,
            data = data,
            image = image,
            username = authentication.name
        ).let {
            ReportDto.fromEntity(it)
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseMessage("Report deleted successfully.")
    fun deleteReport(
        @PathVariable id: String,
        authentication: Authentication
    ) {
        reportService.deleteReport(
            id = id,
            username = authentication.name
        )
    }
}