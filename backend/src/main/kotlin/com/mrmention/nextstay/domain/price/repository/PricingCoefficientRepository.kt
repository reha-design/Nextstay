package com.mrmention.nextstay.domain.price.repository

import com.mrmention.nextstay.domain.price.entity.PricingCoefficient
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PricingCoefficientRepository : JpaRepository<PricingCoefficient, UUID> {
    fun findByKey(key: String): PricingCoefficient?
}
