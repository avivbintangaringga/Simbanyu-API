package com.avivba.simbanyu.api.features.prices

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PriceRepository : JpaRepository<Price, UUID>