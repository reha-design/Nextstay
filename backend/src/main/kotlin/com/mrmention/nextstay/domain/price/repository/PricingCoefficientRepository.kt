package com.mrmention.nextstay.domain.price.repository

import com.mrmention.nextstay.domain.price.entity.PricingCoefficient
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PricingCoefficientRepository : JpaRepository<PricingCoefficient, String> {
    fun findByKey(key: String): Optional<PricingCoefficient>
}
