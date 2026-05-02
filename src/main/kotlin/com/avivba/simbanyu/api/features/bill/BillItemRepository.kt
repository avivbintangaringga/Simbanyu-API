package com.avivba.simbanyu.api.features.bill

import com.avivba.simbanyu.api.features.bill.model.BillItem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BillItemRepository: JpaRepository<BillItem, UUID> {
}