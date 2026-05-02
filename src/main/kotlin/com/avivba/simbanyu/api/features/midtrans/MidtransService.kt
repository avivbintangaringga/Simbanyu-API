package com.avivba.simbanyu.api.features.midtrans

import com.avivba.simbanyu.api.config.MidtransProperties
import com.avivba.simbanyu.api.features.bill.Bill
import com.avivba.simbanyu.api.features.midtrans.model.Callback
import com.avivba.simbanyu.api.features.midtrans.model.CustomerDetails
import com.avivba.simbanyu.api.features.midtrans.model.ItemDetails
import com.avivba.simbanyu.api.features.midtrans.model.SnapTransactionRequest
import com.avivba.simbanyu.api.features.midtrans.model.SnapTransactionResponse
import com.avivba.simbanyu.api.features.midtrans.model.TransactionDetails
import com.avivba.simbanyu.api.utils.getMonthName
import com.avivba.simbanyu.api.utils.getYear
import com.github.f4b6a3.ulid.UlidCreator
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.Base64

@Service
class MidtransService(
    private val midtransProperties: MidtransProperties,
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


        return response.redirectUrl
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