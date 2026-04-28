package com.avivba.simbanyu.api.features.bill

import org.springframework.data.jpa.repository.JpaRepository

interface BillRepository : JpaRepository<Bill, String> {
}