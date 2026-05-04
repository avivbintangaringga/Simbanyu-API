package com.avivba.simbanyu.api.features.midtrans

import com.avivba.simbanyu.api.config.properties.MidtransProperties
import com.avivba.simbanyu.api.features.bill.model.Bill
import com.avivba.simbanyu.api.features.bill.BillRepository
import com.avivba.simbanyu.api.features.bill.model.BillStatus
import com.avivba.simbanyu.api.features.midtrans.model.Callback
import com.avivba.simbanyu.api.features.midtrans.model.CustomerDetails
import com.avivba.simbanyu.api.features.midtrans.model.ItemDetails
import com.avivba.simbanyu.api.features.midtrans.model.PaymentCallbackRequest
import com.avivba.simbanyu.api.features.midtrans.model.SnapTransactionRequest
import com.avivba.simbanyu.api.features.midtrans.model.SnapTransactionResponse
import com.avivba.simbanyu.api.features.midtrans.model.TransactionDetails
import com.avivba.simbanyu.api.features.notification.NotificationService
import com.avivba.simbanyu.api.utils.getMonthName
import com.avivba.simbanyu.api.utils.getYear
import com.avivba.simbanyu.api.utils.sha512
import com.github.f4b6a3.ulid.UlidCreator
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.Base64

@Service
class MidtransService(
    private val midtransPaymentRepository: MidtransPaymentRepository,
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

    fun requestSnapPayment(
        bills: List<Bill>,
        callbackUrl: String
    ): String {
        if (bills.isEmpty())
            throw IllegalArgumentException("Bills cannot be empty")

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
            MidtransPayment(
                orderId = orderId,
                bill = bill
            )
        }

        midtransPaymentRepository.saveAll(payments)

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

        val payments = midtransPaymentRepository.findMidtransPaymentByOrderId(data.orderId)

        if (payments.isEmpty()) {
            throw EntityNotFoundException("Payment not found")
        }

        val bills = payments.map { it.bill }
        val months = bills.map {
            "${it.createdAt.getMonthName()} ${it.createdAt.getYear()}"
        }.distinct().joinToString(", ")

        when (data.transactionStatus) {
            "pending" -> {
                // Do nothing for now
            }

            "capture", "settlement" -> {
                bills.forEach { bill -> bill.status = BillStatus.PAID }
                billRepository.saveAll(bills)

                notificationService.sendNotificationToUser(
                    title = "Tagihan Selesai Dibayar",
                    body = "Tagihan bulan ${months} telah berhasil dibayar",
                    user = bills[0].subscriber.user
                )
            }

            "deny", "failure", "expire", "cancel" -> {
                bills.forEach { bill -> bill.status = BillStatus.UNPAID }
                billRepository.saveAll(bills)

                notificationService.sendNotificationToUser(
                    title = "Tagihan Gagal Dibayar",
                    body = "Tagihan bulan ${months} gagal dibayarkan",
                    user = bills[0].subscriber.user
                )
            }

            "refund", "partial_refund" -> {
                bills.forEach { bill -> bill.status = BillStatus.UNPAID }
                billRepository.saveAll(bills)

                notificationService.sendNotificationToUser(
                    title = "Uang Dikembalikan",
                    body = "Pembayaran tagihan bulan ${months} telah dikembalikan",
                    user = bills[0].subscriber.user
                )
            }
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