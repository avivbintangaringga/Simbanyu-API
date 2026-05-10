package com.avivba.simbanyu.api.features.payment

import com.avivba.simbanyu.api.config.properties.MidtransProperties
import com.avivba.simbanyu.api.features.bill.BillRepository
import com.avivba.simbanyu.api.features.bill.model.Bill
import com.avivba.simbanyu.api.features.bill.model.BillStatus
import com.avivba.simbanyu.api.features.payment.model.*
import com.avivba.simbanyu.api.features.notification.NotificationService
import com.avivba.simbanyu.api.features.payment.model.midtrans.CustomerDetails
import com.avivba.simbanyu.api.features.payment.model.midtrans.TransactionDetails
import com.avivba.simbanyu.api.utils.getMonthName
import com.avivba.simbanyu.api.utils.getYear
import com.avivba.simbanyu.api.utils.sha512
import com.github.f4b6a3.ulid.UlidCreator
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.*

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val midtransProperties: MidtransProperties,
    private val notificationService: NotificationService,
    private val billRepository: BillRepository,
) {
    private val snapClient = RestClient.builder()
        .baseUrl(midtransProperties.snapUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic ${getAuthString()}")
        .build()

    @Transactional
    fun requestCashPayment(
        billIds: List<String>,
    ): List<Payment> {
        val billIds = billIds.map { UUID.fromString(it) }.toMutableList()
        val bills = billRepository.findBillsByIdIsIn(
            ids = billIds
        )

        if (bills.isEmpty()) {
            throw IllegalArgumentException("Bills is empty or not found.")
        }

        if (bills.any { it.status == BillStatus.PAID }) {
            throw IllegalArgumentException("Bill is already paid")
        }

        val oldPayments = bills.flatMap { it.payments }

        // Cancel all pending payments
        val payments = oldPayments.map {
            if (it.status == PaymentStatus.PENDING) {
                it.status = PaymentStatus.FAILED
                it
            } else {
                it
            }
        }

        paymentRepository.saveAll(payments)

        val orderId = createTransactionId()
        val result = bills.map { bill ->
            Payment(
                orderId = orderId,
                paymentType = "Cash",
                paymentUrl = null,
                status = PaymentStatus.SUCCESS,
                bill = bill
            )
        }

        val paymentResult = paymentRepository.saveAll(result)

        val months = bills.map {
            "${it.createdAt.getMonthName()} ${it.createdAt.getYear()}"
        }.distinct().joinToString(", ")

        notificationService.sendNotificationToUser(
            title = "Tagihan Selesai Dibayar",
            body = "Tagihan bulan ${months} telah berhasil dibayar",
            user = bills[0].subscriber.user
        )

        return paymentResult
    }

    fun requestPayment(
        billIds: List<String>,
        callbackUrl: String,
    ): String {
        val billIds = billIds.map { UUID.fromString(it) }.toMutableList()
        val bills = billRepository.findBillsByIdIsIn(
            ids = billIds
        )

        if (bills.isEmpty()) {
            throw IllegalArgumentException("Bills is empty or not found.")
        }

        if (bills.any { it.status != BillStatus.UNPAID }) {
            throw IllegalArgumentException("Bills must be unpaid to request payment.")
        }

        val orderId = createTransactionId()

        val transactionDetails = TransactionDetails(
            orderId = orderId,
            grossAmount = bills.sumOf { it.amount.toInt() }
        )

        val customerDetails = CustomerDetails(
            firstName = bills[0].subscriber.user.name,
            lastName = "(${bills[0].subscriber.meterId})",
            phone = bills[0].subscriber.user.phoneNumber
        )

        val items = bills.map {
            ItemDetails(
                id = it.id.toString(),
                price = it.amount.toInt(),
                quantity = 1,
                name = "Tagihan ${it.createdAt.getMonthName()} ${it.createdAt.getYear()}"
            )
        }

        val callback = Callback(callbackUrl)

        val requestBody = SnapTransactionRequest(
            transactionDetails = transactionDetails,
            callbacks = callback,
            itemDetails = items,
            customerDetails = customerDetails
        )

        val response = snapClient.post()
            .uri("/snap/v1/transactions")
            .body(requestBody)
            .retrieve()
            .body<SnapTransactionResponse>()
            ?: throw Exception("Failed to create Snap payment")

        billRepository.saveAll(
            bills.map {
                it.status = BillStatus.PENDING
                it
            })

        val payments = bills.map { bill ->
            Payment(
                orderId = orderId,
                bill = bill,
                paymentUrl = response.redirectUrl,
            )
        }

        paymentRepository.saveAll(payments)

        notificationService.sendNotificationToUser(
            title = "Permintaan pembayaran",
            body = "Permintaan pembayaran berhasil dilakukan, silahkan lakukan pembayaran",
            user = bills[0].subscriber.user
        )

        return response.redirectUrl
    }

    fun handleCallback(
        data: PaymentCallbackRequest
    ) {
        val isSignatureValid = checkSignature(
            data.signatureKey,
            data.orderId,
            data.transactionStatus,
            data.grossAmount
        )

        if (!isSignatureValid) {
            throw IllegalArgumentException("Invalid notification signature")
        }

        val payments = paymentRepository.findPaymentByOrderId(data.orderId)

        if (payments.isEmpty()) {
            throw EntityNotFoundException("Payment not found")
        }

        val bills = payments.map { it.bill }
        val months = bills.map {
            "${it.createdAt.getMonthName()} ${it.createdAt.getYear()}"
        }.distinct().joinToString(", ")

        var updatedPayments = payments.map {
            it.paymentType = mapPaymentType(data.paymentType)
            it
        }

        when (data.transactionStatus) {
            "pending" -> {
                updatedPayments = updatedPayments.map {
                    it.status = PaymentStatus.PENDING
                    it
                }
                paymentRepository.saveAll(updatedPayments)
            }

            "capture", "settlement" -> {
                bills.forEach { bill -> bill.status = BillStatus.PAID }
                billRepository.saveAll(bills)

                updatedPayments = updatedPayments.map {
                    it.paymentType = data.paymentType
                    it.status = PaymentStatus.SUCCESS
                    it
                }

                paymentRepository.saveAll(updatedPayments)

                notificationService.sendNotificationToUser(
                    title = "Tagihan Selesai Dibayar",
                    body = "Tagihan bulan ${months} telah berhasil dibayar",
                    user = bills[0].subscriber.user
                )
            }

            "deny", "failure", "expire", "cancel" -> {
                bills.forEach { bill -> bill.status = BillStatus.UNPAID }
                billRepository.saveAll(bills)

                updatedPayments = updatedPayments.map {
                    it.status = PaymentStatus.FAILED
                    it
                }

                paymentRepository.saveAll(updatedPayments)

                notificationService.sendNotificationToUser(
                    title = "Tagihan Gagal Dibayar",
                    body = "Tagihan bulan ${months} gagal dibayarkan",
                    user = bills[0].subscriber.user
                )
            }

            "refund", "partial_refund" -> {
                bills.forEach { bill -> bill.status = BillStatus.UNPAID }
                billRepository.saveAll(bills)

                updatedPayments = updatedPayments.map {
                    it.status = PaymentStatus.REFUND
                    it
                }
                paymentRepository.saveAll(updatedPayments)

                notificationService.sendNotificationToUser(
                    title = "Uang Dikembalikan",
                    body = "Pembayaran tagihan bulan ${months} telah dikembalikan",
                    user = bills[0].subscriber.user
                )
            }
        }
    }

    private fun mapPaymentType(type: String?): String {
        return when (type) {
            null -> "Belum dipilih"
            "credit_card" -> "Kartu kredit"
            "gopay" -> "GoPay"
            "qris" -> "QRIS"
            "shopeepay" -> "ShopeePay"
            "bank_transfer" -> "Transfer Bank"
            "echannel" -> "Mandiri Bill"
            "akulaku" -> "Akulaku"
            "cstore" -> "Retail"
            else -> "Lainnya"
        }
    }

    private fun checkSignature(
        signatureKey: String,
        orderId: String,
        statusCode: String,
        grossAmount: String
    ): Boolean {
        val calculatedSignature = sha512(orderId + statusCode + grossAmount + midtransProperties.serverKey)
        return calculatedSignature == signatureKey
    }

    private fun createTransactionId(): String {
        val builder = StringBuilder()
        builder.append("SMB-")
        builder.append(UlidCreator.getUlid())
        return builder.toString()
    }

    private fun getAuthString(): String {
        val auth = "${midtransProperties.serverKey}:"
        return Base64.getEncoder().encodeToString(auth.toByteArray())
    }
}