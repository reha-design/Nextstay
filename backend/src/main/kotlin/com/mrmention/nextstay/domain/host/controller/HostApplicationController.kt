package com.mrmention.nextstay.domain.host.controller

import com.mrmention.nextstay.domain.host.dto.HostApplicationRequest
import com.mrmention.nextstay.domain.host.dto.HostApplicationResponse
import com.mrmention.nextstay.domain.host.service.HostApplicationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Host Application", description = "호스트 입점 신청 및 관리 API")
@RestController
@RequestMapping("/api/v1/host/applications")
class HostApplicationController(
    private val hostApplicationService: HostApplicationService
) {

    @Operation(summary = "호스트 입점 신청", description = "로그인한 게스트가 호스트 권한을 얻기 위해 입점 신청을 합니다.")
    @PostMapping
    fun submitApplication(
        @AuthenticationPrincipal userNo: String,
        @Valid @RequestBody request: HostApplicationRequest
    ): ResponseEntity<String> {
        val id = hostApplicationService.submitApplication(userNo, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(id)
    }

    @Operation(summary = "내 입점 신청 상태 조회", description = "현재 본인이 신청한 입점 심사 상태를 조회합니다.")
    @GetMapping("/me")
    fun getMyApplication(
        @AuthenticationPrincipal userNo: String
    ): ResponseEntity<HostApplicationResponse> {
        val response = hostApplicationService.getMyApplication(userNo)
        return ResponseEntity.ok(response)
    }
}
