package com.avivba.simbanyu.api.features.report

import org.springframework.data.jpa.repository.JpaRepository

interface ReportRepository : JpaRepository<Report, String> {
}