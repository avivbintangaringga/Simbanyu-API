package com.avivba.simbanyu.api.features.bill.model

data class BillItemDto(
    val id: String,
    val name: String,
    val amount: Double,
) {
    companion object {
        fun fromEntity(billItem: BillItem) = BillItemDto(
            id = billItem.id.toString(),
            name = billItem.name,
            amount = billItem.amount,
        )
    }
}