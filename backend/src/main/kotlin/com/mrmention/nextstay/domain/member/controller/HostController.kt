package com.mrmention.nextstay.domain.member.controller

import com.mrmention.nextstay.domain.member.dto.AuthResponse
import com.mrmention.nextstay.domain.member.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Host", description = "호스트(B2B) 관련 API")
@RestController
@RequestMapping("/api/v1/hosts")
class HostController(
    private val authService: AuthService
) {

    @Operation(summary = "호스트 온보딩 완료 처리", description = "호스트의 온보딩(특례 신청) 상태를 완료로 변경하고 갱신된 토큰을 반환합니다.")
    @PostMapping("/onboarding/complete")
    fun completeOnboarding(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<AuthResponse> {
        val response = authService.completeOnboarding(userDetails.username)
        return ResponseEntity.ok(response)
    }
}
