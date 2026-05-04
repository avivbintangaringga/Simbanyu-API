package com.avivba.simbanyu.api.features.report

import com.avivba.simbanyu.api.features.report.model.Report
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReportRepository : JpaRepository<Report, UUID> {
    fun findReportsBySubscriber_Id(subscriberId: UUID): MutableList<Report>
}