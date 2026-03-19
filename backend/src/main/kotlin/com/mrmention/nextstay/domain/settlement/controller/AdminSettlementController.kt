package com.mrmention.nextstay.domain.settlement.controller

import com.mrmention.nextstay.domain.settlement.dto.SettlementResponse
import com.mrmention.nextstay.domain.settlement.entity.SettlementStatus
import com.mrmention.nextstay.domain.settlement.service.AdminSettlementService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Tag(name = "Admin Settlement API", description = "관리자용 정산 관리 API")
@RestController
@RequestMapping("/admin/v1/settlements")
@PreAuthorize("hasRole('ADMIN')")
class AdminSettlementController(
    private val adminSettlementService: AdminSettlementService
) {
    @Operation(summary = "전체 정산 내역 조회", description = "시스템의 모든 호스트 정산 내역을 조회합니다.")
    @GetMapping
    fun getAllSettlements(pageable: org.springframework.data.domain.Pageable): ResponseEntity<org.springframework.data.domain.Page<SettlementResponse>> {
        val response = adminSettlementService.getAllSettlements(pageable)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "정산 상태 업데이트", description = "특정 정산 내역의 상태(Pending/Completed)를 변경합니다.")
    @PutMapping("/{id}/status")
    fun updateSettlementStatus(
        @PathVariable id: UUID,
        @RequestParam status: SettlementStatus
    ): ResponseEntity<SettlementResponse> {
        val response = adminSettlementService.updateSettlementStatus(id, status)
        return ResponseEntity.ok(response)
    }
}
