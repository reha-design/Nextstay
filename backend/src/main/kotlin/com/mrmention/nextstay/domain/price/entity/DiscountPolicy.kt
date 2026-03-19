package com.mrmention.nextstay.domain.price.entity

import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

enum class DiscountPolicyType { LONG_STAY, EARLY_BIRD, FLASH_SALE, FIX_AMOUNT }

@Entity
@Table(name = "discount_policies")
class DiscountPolicy(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @Column(name = "policy_name", nullable = false, length = 100)
    val policyName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false, length = 20)
    val policyType: DiscountPolicyType,

    @Column(name = "min_stay_nights")
    val minStayNights: Int? = 0,

    @Column(name = "discount_rate", precision = 5, scale = 2)
    val discountRate: BigDecimal? = null,

    @Column(name = "discount_amount")
    val discountAmount: Int? = null,

    @Column(name = "start_date")
    val startDate: LocalDate? = null,

    @Column(name = "end_date")
    val endDate: LocalDate? = null,

    @Column(name = "badge_text", length = 50)
    val badgeText: String? = null,

    @Column(name = "priority")
    val priority: Int? = 1
) : BaseEntity()
