package com.mrmention.nextstay.domain.cs.controller

import com.mrmention.nextstay.domain.cs.dto.CsTicketRequest
import com.mrmention.nextstay.domain.cs.dto.CsTicketResponse
import com.mrmention.nextstay.domain.cs.service.CsTicketService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@Tag(name = "CS Ticket API", description = "게스트용 민원/문의 API")
@RestController
@RequestMapping("/api/v1/tickets")
class CsTicketController(
    private val csTicketService: CsTicketService
) {
    @Operation(summary = "1:1 문의 생성", description = "특정 예약에 대한 문의 혹은 일반 문의를 생성합니다.")
    @PostMapping
    fun createTicket(
        @Valid @RequestBody request: CsTicketRequest,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<String> {
        val ticketId = csTicketService.createTicket(user.username, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketId)
    }

    @Operation(summary = "내 문의 내역 조회", description = "내가 작성한 CS 티켓 목록을 페이징하여 조회합니다.")
    @GetMapping("/me")
    fun getMyTickets(
        @AuthenticationPrincipal userNo: String,
        pageable: Pageable
    ): ResponseEntity<Page<CsTicketResponse>> {
        val response = csTicketService.getMyTickets(userNo, pageable)
        return ResponseEntity.ok(response)
    }
}
