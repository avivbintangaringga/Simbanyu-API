package com.avivba.simbanyu.api.features.bill

import com.avivba.simbanyu.api.features.bill.model.BillItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.*

interface BillItemRepository: JpaRepository<BillItem, UUID> {
    @Modifying
    @Query("UPDATE BillItem b SET b.createdAt = :date, b.updatedAt = :date WHERE b.id = :id")
    fun updateTimestamps(id: UUID, date: Instant)

    @Modifying
    @Query("UPDATE BillItem b SET b.createdAt = :date, b.updatedAt = :date WHERE b.id IN :ids")
    fun updateTimestampsInBatch(ids: List<UUID>, date: Instant)
}