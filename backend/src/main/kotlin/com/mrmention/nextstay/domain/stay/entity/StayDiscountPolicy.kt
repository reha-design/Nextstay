package com.mrmention.nextstay.domain.stay.entity

import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "stay_discount_policies")
class StayDiscountPolicy(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stay_id", nullable = false)
    val stay: Stay,

    @Column(name = "min_nights", nullable = false)
    val minNights: Int,

    @Column(name = "discount_rate", nullable = false, precision = 5, scale = 3)
    val discountRate: java.math.BigDecimal // 0.0 ~ 1.0 (예: 0.33 -> 33% 할인)
) : BaseEntity()
