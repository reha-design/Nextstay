package com.mrmention.nextstay.domain.host.controller

import com.mrmention.nextstay.domain.host.dto.HostApplicationResponse
import com.mrmention.nextstay.domain.host.entity.HostApplicationStatus
import com.mrmention.nextstay.domain.host.service.HostApplicationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Tag(name = "Admin Host Management", description = "관리자용 호스트 신청 관리 API")
@RestController
@RequestMapping("/api/v1/admin/host-applications")
class AdminHostApplicationController(
    private val hostApplicationService: HostApplicationService
) {

    @Operation(summary = "전체 입점 신청 목록 조회", description = "관리자가 모든 호스트 입점 신청 내역을 조회합니다.")
    @GetMapping
    fun getAllApplications(): ResponseEntity<List<HostApplicationResponse>> {
        val applications = hostApplicationService.getAllApplications()
        return ResponseEntity.ok(applications)
    }

    @Operation(summary = "입점 신청 상태 변경", description = "관리자가 특정 입점 신청을 승인하거나 반려합니다.")
    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: UUID,
        @RequestParam status: HostApplicationStatus
    ): ResponseEntity<HostApplicationResponse> {
        val result = hostApplicationService.updateApplicationStatus(id, status)
        return ResponseEntity.ok(result)
    }
}
