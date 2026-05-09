package com.avivba.simbanyu.api.features.payment.model

data class PaymentDto(
    val id: String,
    val orderId: String,
    val paymentType: String?,
    val paymentUrl: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun fromEntity(payment: Payment) = PaymentDto(
            id = payment.id.toString(),
            orderId = payment.orderId,
            paymentType = payment.paymentType,
            paymentUrl = payment.paymentUrl,
            status = payment.status.name,
            createdAt = payment.createdAt.toString(),
            updatedAt = payment.updatedAt.toString(),
        )
    }
}
