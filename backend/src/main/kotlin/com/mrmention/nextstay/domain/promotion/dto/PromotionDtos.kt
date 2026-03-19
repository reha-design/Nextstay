package com.mrmention.nextstay.domain.promotion.dto

import com.mrmention.nextstay.domain.promotion.entity.DiscountType
import java.time.LocalDateTime

data class PromoRuleRequest(
    val name: String,
    val discountType: DiscountType,
    val amount: Long,
    val minOrderPrice: Long = 0,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val maxPerUser: Int = 1
)

data class PromoRuleResponse(
    val id: String,
    val name: String,
    val discountType: DiscountType,
    val amount: Long,
    val minOrderPrice: Long,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val maxPerUser: Int
)

data class CouponResponse(
    val id: String,
    val promoRuleName: String,
    val isUsed: Boolean,
    val usedAt: LocalDateTime?,
    val issuedAt: LocalDateTime
)
