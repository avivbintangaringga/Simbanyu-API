package com.avivba.simbanyu.api.features.report

import com.avivba.simbanyu.api.features.activity.ActivityService
import com.avivba.simbanyu.api.features.notification.NotificationService
import com.avivba.simbanyu.api.features.notification.model.BroadcastTopic
import com.avivba.simbanyu.api.features.report.model.AddReportRequest
import com.avivba.simbanyu.api.features.report.model.HandlingRequest
import com.avivba.simbanyu.api.features.report.model.Report
import com.avivba.simbanyu.api.features.report.model.ReportStatus
import com.avivba.simbanyu.api.features.subscriber.SubscriberRepository
import com.avivba.simbanyu.api.features.upload.UploadService
import com.avivba.simbanyu.api.features.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ReportService(
    private val reportRepository: ReportRepository,
    private val subscriberRepository: SubscriberRepository,
    private val notificationsService: NotificationService,
    private val uploadService: UploadService,
    private val userRepository: UserRepository,
    private val activityService: ActivityService,
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
        data: AddReportRequest,
        image: MultipartFile,
        username: String,
    ): Report {
        val subscriber = subscriberRepository.findSubscriberByUser_Username(username).firstOrNull()
            ?: throw IllegalArgumentException("User is not a subscriber")

        val image = uploadService.saveMultipart(image)
        val report = Report(
            address = data.address,
            longitude = data.longitude,
            latitude = data.latitude,
            description = data.description,
            evidenceImage = image,
            subscriber = subscriber
        )

        notificationsService.sendNotificationToUser(
            title = "Laporan Pengaduan",
            body = "Laporan pengaduan anda telah dikirimkan, mohon tunggu penanganan dari kami.",
            user = subscriber.user
        )

        notificationsService.sendBroadcastNotification(
            title = "Laporan Pengaduan",
            body = "Laporan pengaduan baru telah diterima dari ${subscriber.user.name}",
            BroadcastTopic.OPERATOR
        )

        notificationsService.sendBroadcastNotification(
            title = "Laporan Pengaduan",
            body = "Laporan pengaduan baru telah diterima dari ${subscriber.user.name}",
            BroadcastTopic.ADMIN
        )

        return reportRepository.save(report)
    }

    fun handleReport(
        id: String,
        data: HandlingRequest,
        image: MultipartFile?,
        username: String,
    ): Report {
        val user = userRepository.findByUsername(username)
        val report = reportRepository.findById(UUID.fromString(id)).orElseThrow()

        if (image != null) {
            if (report.handlingEvidenceImage != null) {
                uploadService.deleteFile(report.handlingEvidenceImage!!)
            }
            report.handlingEvidenceImage = uploadService.saveMultipart(image)
        }

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

        activityService.recordActivity(
            user = user,
            title = "Proses Laporan #${report.id}",
            body = "Laporan telah diproses. Status: ${data.status}.",
        )

        return reportRepository.save(report)
    }

    fun deleteReport(
        id: String,
        username: String,
    ) {
        val user = userRepository.findByUsername(username)
        val report = reportRepository.findById(UUID.fromString(id)).orElseThrow()
        reportRepository.delete(report)
        activityService.recordActivity(
            user = user,
            title = "Hapus Laporan",
            body = "Laporan ${id} telah dihapus.",
        )
    }
}