package com.mrmention.nextstay.domain.stay.controller

import com.mrmention.nextstay.domain.stay.dto.StayRequest
import com.mrmention.nextstay.domain.stay.dto.StayResponse
import com.mrmention.nextstay.domain.stay.service.StayService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@Tag(name = "Stay", description = "숙소 관리 API")
@RestController
@RequestMapping("/api/v1/stays")
class StayController(
    private val stayService: StayService
) {

    @Operation(summary = "신규 숙소 등록", description = "호스트 권한으로 새로운 숙소를 등록합니다.")
    @PostMapping
    fun createStay(
        @Valid @RequestBody request: StayRequest,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<String> {
        val stayNo = stayService.createStay(request, user.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(stayNo)
    }

    @Operation(summary = "숙소 전체 목록 조회", description = "시스템에 등록된 모든 숙소 목록을 조회합니다.")
    @GetMapping
    fun getAllStays(): ResponseEntity<List<StayResponse>> {
        val response = stayService.getAllStays()
        return ResponseEntity.ok(response)
    }
}
