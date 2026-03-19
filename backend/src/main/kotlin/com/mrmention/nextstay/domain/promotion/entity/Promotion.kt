package com.mrmention.nextstay.domain.promotion.entity

import com.mrmention.nextstay.domain.member.entity.Member
import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

enum class DiscountType { FIXED, RATE }

@Entity
@Table(name = "promo_rules")
class PromoRule(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @Column(nullable = false)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    val discountType: DiscountType,

    @Column(nullable = false)
    val amount: Long,

    @Column(name = "min_order_price", nullable = false)
    val minOrderPrice: Long = 0,

    @Column(name = "start_at", nullable = false)
    val startAt: LocalDateTime,

    @Column(name = "end_at", nullable = false)
    val endAt: LocalDateTime,

    @Column(name = "max_per_user", nullable = false)
    val maxPerUser: Int = 1
) : BaseEntity()

@Entity
@Table(name = "coupons")
class Coupon(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_rule_id", nullable = false)
    val promoRule: PromoRule,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Column(name = "is_used", nullable = false)
    var isUsed: Boolean = false,

    @Column(name = "used_at")
    var usedAt: LocalDateTime? = null,

    @Column(name = "issued_at", nullable = false)
    val issuedAt: LocalDateTime = LocalDateTime.now()
) : BaseEntity()
