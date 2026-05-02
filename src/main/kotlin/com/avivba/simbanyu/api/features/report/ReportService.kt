package com.avivba.simbanyu.api.features.report

import com.avivba.simbanyu.api.features.notification.NotificationService
import com.avivba.simbanyu.api.features.report.model.AddReportRequest
import com.avivba.simbanyu.api.features.report.model.HandlingRequest
import com.avivba.simbanyu.api.features.report.model.Report
import com.avivba.simbanyu.api.features.report.model.ReportStatus
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ReportService(
    private val reportRepository: ReportRepository,
    private val subscriberRepository: SubscriberRepository,
    private val notificationsService: NotificationService
) {
    fun getAllReports(): List<Report> = reportRepository.findAll()

    fun getReportById(id: String): Report {
        return reportRepository.findById(UUID.fromString(id)).orElseThrow()
    }

    fun getReportBySubscriberId(
        subscriberId: String
    ): List<Report> {
        return reportRepository.findReportsBySubscriber_Id(UUID.fromString(subscriberId))
    }

    fun addReport(
        subscriberId: String,
        data: AddReportRequest
    ): Report {
        val subscriber = subscriberRepository.findById(UUID.fromString(subscriberId)).orElseThrow()
        // TODO: Store image and get URL
        val imageUrl = ""
        val report = Report(
            address = data.address,
            longitude = data.longitude,
            latitude = data.latitude,
            description = data.description,
            evidenceImageUrl = imageUrl,
            subscriber = subscriber
        )

        notificationsService.sendNotificationToUser(
            title = "Laporan Pengaduan",
            body = "Laporan pengaduan anda telah dikirimkan, mohon tunggu penanganan dari kami.",
            user = subscriber.user
        )

        return reportRepository.save(report)
    }

    fun handleReport(
        id: String,
        data: HandlingRequest
    ): Report {
        val report = reportRepository.findById(UUID.fromString(id)).orElseThrow()

        // TODO: Store image and get URL
        val imageUrl = ""
        report.handlingEvidenceImageUrl = imageUrl
        report.status = data.status
        report.operatorNotes = data.operatorNotes

        when (data.status) {
            ReportStatus.IN_PROGRESS -> {
                notificationsService.sendNotificationToUser(
                    title = "Laporan Sedang Diproses",
                    body = "Laporan anda sedang ditangani, mohon tunggu.",
                    user = report.subscriber.user
                )
            }

            ReportStatus.COMPLETED -> {
               notificationsService.sendNotificationToUser(
                   title = "Laporan Selesai Ditangani",
                   body = "Laporan anda telah selesai ditangani, terima kasih telah menggunakan layanan kami.",
                   user = report.subscriber.user
               )
            }

            ReportStatus.REJECTED -> {
                notificationsService.sendNotificationToUser(
                    title = "Laporan Ditolak",
                    body = "Laporan anda ditolak, pastikan informasi yang anda berikan telah sesuai.",
                    user = report.subscriber.user
                )
            }

            else -> {
                // Do nothing
            }
        }

        return reportRepository.save(report)
    }

    fun deleteReport(id: String) {
        reportRepository.deleteById(UUID.fromString(id))
    }
}