package com.avivba.simbanyu.api.database.repository

import com.avivba.simbanyu.api.database.entity.Report
import org.springframework.data.jpa.repository.JpaRepository

interface ReportRepository : JpaRepository<Report, String> {
}