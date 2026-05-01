package com.avivba.simbanyu.api.features.report

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ReportRepository : JpaRepository<Report, UUID> {
    fun findReportsBySubscriber_Id(subscriberId: UUID): MutableList<Report>
}