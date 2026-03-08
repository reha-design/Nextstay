package com.mrmention.nextstay.domain.price.dto

import java.time.LocalDate

data class PriceCalculationRequest(
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate
)

data class PriceCalculationResponse(
    val totalNights: Int,
    val basePricePerNight: Int,
    val totalBasePrice: Long,
    val seasonalAdjustedPrice: Long,
    val discountAmount: Long,
    val finalTotalPrice: Long,
    val appliedSeasons: List<String> = emptyList(),
    val appliedDiscount: String? = null
)
