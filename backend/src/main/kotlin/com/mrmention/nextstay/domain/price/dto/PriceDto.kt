package com.mrmention.nextstay.domain.price.dto

import java.math.BigDecimal
import java.time.LocalDate

data class PriceCalculationRequest(
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val guestCount: Int = 2
)

data class PricingDetailDto(
    val nightlyBasePrice: Int,
    val totalNights: Int,
    val totalOriginalPrice: Long,
    val finalTotalPrice: Long,
    val totalDiscountAmount: Long,
    val totalDiscountRate: Int,
    val pricePerNightDiscounted: Int,
    val currency: String = "KRW"
)

data class DisplayDto(
    val badgeText: String?,
    val discountNotice: String?,
    val isLongStayDiscount: Boolean,
    val formattedOriginalPrice: String,
    val formattedFinalPrice: String
)

data class AppliedPolicyDto(
    val name: String,
    val rate: BigDecimal? = null,
    val amount: Int? = null
)

data class PriceCalculationResponse(
    val pricing: PricingDetailDto,
    val display: DisplayDto,
    val appliedPolicies: List<AppliedPolicyDto>
)
