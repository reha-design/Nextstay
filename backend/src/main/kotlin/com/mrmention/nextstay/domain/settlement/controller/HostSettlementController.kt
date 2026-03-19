package com.mrmention.nextstay.domain.settlement.controller

import com.mrmention.nextstay.domain.settlement.dto.SettlementResponse
import com.mrmention.nextstay.domain.settlement.service.SettlementService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Host Settlement API", description = "호스트용 정산 내역 조회 API")
@RestController
@RequestMapping("/api/v1/host/settlements")
@PreAuthorize("hasRole('HOST')")
class HostSettlementController(
    private val settlementService: SettlementService
) {
    @Operation(summary = "내 정산 내역 조회", description = "로그인한 호스트의 정산 목록을 페이징하여 조회합니다.")
    @GetMapping("/me")
    fun getMySettlements(
        @AuthenticationPrincipal userNo: String,
        pageable: Pageable
    ): ResponseEntity<Page<SettlementResponse>> {
        val response = settlementService.getMySettlements(userNo, pageable)
        return ResponseEntity.ok(response)
    }
}
