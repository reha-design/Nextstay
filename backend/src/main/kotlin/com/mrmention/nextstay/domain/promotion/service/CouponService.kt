package com.mrmention.nextstay.domain.promotion.service

import com.mrmention.nextstay.domain.promotion.dto.CouponResponse
import com.mrmention.nextstay.domain.promotion.entity.Coupon
import com.mrmention.nextstay.domain.promotion.repository.CouponRepository
import com.mrmention.nextstay.domain.member.repository.MemberRepository
import com.mrmention.nextstay.global.exception.BusinessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CouponService(
    private val couponRepository: CouponRepository,
    private val memberRepository: MemberRepository
) {
    fun getMyCoupons(userNo: String, pageable: Pageable): Page<CouponResponse> {
        val member = memberRepository.findByUserNo(UUID.fromString(userNo))
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.")

        return couponRepository.findByMemberIdAndIsUsed(member.id, false, pageable)
            .map { it.toResponse() }
    }

    private fun Coupon.toResponse() = CouponResponse(
        id = this.id.toString(),
        promoRuleName = this.promoRule.name,
        isUsed = this.isUsed,
        usedAt = this.usedAt,
        issuedAt = this.issuedAt
    )
}
