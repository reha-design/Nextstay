package com.mrmention.nextstay.domain.price.entity

import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal

/**
 * 시스템 전역에서 사용하는 가격 관련 계수(할증률, 기본 할인율 등)를 관리하는 엔티티
 */
@Entity
@Table(name = "pricing_coefficients")
class PricingCoefficient(
    @Id
    @Column(name = "coefficient_key", length = 50)
    val key: String,

    @Column(name = "coefficient_value", nullable = false, precision = 10, scale = 4)
    var value: BigDecimal,

    @Column(length = 255)
    val description: String? = null
) : BaseEntity()
