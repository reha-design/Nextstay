package com.mrmention.nextstay.domain.promotion.repository

import com.mrmention.nextstay.domain.promotion.entity.Coupon
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CouponRepository : JpaRepository<Coupon, UUID> {
    fun findByMemberIdAndIsUsed(memberId: UUID, isUsed: Boolean, pageable: Pageable): Page<Coupon>
    fun findAllByMemberId(memberId: UUID): List<Coupon>
    fun countByMemberIdAndPromoRuleId(memberId: UUID, promoRuleId: UUID): Int
}
