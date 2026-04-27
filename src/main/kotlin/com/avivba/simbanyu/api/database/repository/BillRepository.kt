package com.avivba.simbanyu.api.database.repository

import com.avivba.simbanyu.api.database.entity.Bill
import org.springframework.data.jpa.repository.JpaRepository

interface BillRepository : JpaRepository<Bill, String> {
}