package com.mrmention.nextstay.domain.cs.controller

import com.mrmention.nextstay.domain.cs.dto.CsTicketResponse
import com.mrmention.nextstay.domain.cs.entity.CsTicketStatus
import com.mrmention.nextstay.domain.cs.service.AdminCsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Tag(name = "Admin CS API", description = "관리자용 운영 및 CS 관리 API")
@RestController
@RequestMapping("/admin/v1/tickets")
@PreAuthorize("hasRole('ADMIN')")
class AdminCsController(
    private val adminCsService: AdminCsService
) {
    @Operation(summary = "전체 민원 티켓 조회", description = "시스템의 모든 CS 티켓 목록을 조회합니다.")
    @GetMapping
    fun getAllTickets(pageable: org.springframework.data.domain.Pageable): ResponseEntity<org.springframework.data.domain.Page<CsTicketResponse>> {
        val response = adminCsService.getAllTickets(pageable)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "티켓 상태 업데이트", description = "특정 CS 티켓의 해결 여부 상태를 변경합니다.")
    @PutMapping("/{id}/status")
    fun updateTicketStatus(
        @PathVariable id: UUID,
        @RequestParam status: CsTicketStatus
    ): ResponseEntity<CsTicketResponse> {
        val response = adminCsService.updateTicketStatus(id, status)
        return ResponseEntity.ok(response)
    }
}
