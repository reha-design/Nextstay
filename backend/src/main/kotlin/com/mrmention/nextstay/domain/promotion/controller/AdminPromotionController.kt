package com.mrmention.nextstay.domain.promotion.controller

import com.mrmention.nextstay.domain.promotion.dto.PromoRuleRequest
import com.mrmention.nextstay.domain.promotion.dto.PromoRuleResponse
import com.mrmention.nextstay.domain.promotion.service.AdminPromotionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@Tag(name = "Admin Promotion API", description = "관리자용 프로모션 및 쿠폰 관리 API")
@RestController
@RequestMapping("/admin/v1/coupons")
@PreAuthorize("hasRole('ADMIN')")
class AdminPromotionController(
    private val adminPromotionService: AdminPromotionService
) {
    @Operation(summary = "쿠폰 마스터 룰 생성", description = "새로운 쿠폰 발급 규칙을 생성합니다.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "생성 성공"),
        ApiResponse(responseCode = "400", description = "입력값 오류")
    )
    @PostMapping
    fun createPromoRule(@Valid @RequestBody request: PromoRuleRequest): ResponseEntity<String> {
        val ruleId = adminPromotionService.createPromoRule(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ruleId)
    }

    @Operation(summary = "모든 쿠폰 룰 조회", description = "시스템에 등록된 모든 쿠폰 마스터 룰 목록을 조회합니다.")
    @GetMapping
    fun getAllPromoRules(): ResponseEntity<List<PromoRuleResponse>> {
        val response = adminPromotionService.getAllPromoRules()
        return ResponseEntity.ok(response)
    }
}
