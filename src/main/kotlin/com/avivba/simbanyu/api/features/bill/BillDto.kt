package com.avivba.simbanyu.api.features.bill

import com.avivba.simbanyu.api.utils.getMonthName
import com.avivba.simbanyu.api.utils.getYear

data class BillDto(
    val id: String,
    val title: String,
    val amount: Double,
    val status: String,
    val items: List<BillItemDto>,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun fromEntity(bill: Bill) = BillDto(
            id = bill.id.toString(),
            title = "${bill.createdAt.getMonthName()} ${bill.createdAt.getYear()}",
            amount = bill.amount,
            status = bill.status.name,
            items = bill.billItems.map {
                BillItemDto.fromEntity(it)
            },
            createdAt = bill.createdAt.toString(),
            updatedAt = bill.updatedAt.toString(),
        )
    }
}