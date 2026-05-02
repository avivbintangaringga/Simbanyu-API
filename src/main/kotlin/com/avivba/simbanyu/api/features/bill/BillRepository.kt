package com.avivba.simbanyu.api.features.bill

import com.avivba.simbanyu.api.features.bill.model.Bill
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BillRepository : JpaRepository<Bill, UUID> {
    fun findBillsBySubscriber_Id(subscriberId: UUID): MutableList<Bill>
    fun findBillsByIdIsIn(ids: MutableCollection<UUID>): MutableList<Bill>
}