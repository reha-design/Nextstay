package com.mrmention.nextstay.domain.promotion.controller

import com.mrmention.nextstay.domain.promotion.dto.CouponResponse
import com.mrmention.nextstay.domain.promotion.service.CouponService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Coupon API", description = "게스트용 쿠폰 조회 API")
@RestController
@RequestMapping("/api/v1/coupons")
class CouponController(
    private val couponService: CouponService
) {
    @Operation(summary = "내 쿠폰함 조회", description = "사용 가능한 보유 쿠폰 목록을 페이징하여 조회합니다.")
    @GetMapping("/me")
    fun getMyCoupons(
        @AuthenticationPrincipal userNo: String,
        pageable: Pageable
    ): ResponseEntity<Page<CouponResponse>> {
        val response = couponService.getMyCoupons(userNo, pageable)
        return ResponseEntity.ok(response)
    }
}
