package com.mrmention.nextstay.domain.member.controller

import com.mrmention.nextstay.domain.member.dto.UserResponse
import com.mrmention.nextstay.domain.member.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User", description = "사용자 정보 관련 API")
@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val authService: AuthService
) {

    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자의 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    fun getMe(authentication: Authentication): ResponseEntity<UserResponse> {
        val userNo = authentication.name
        val userResponse = authService.getMemberByUserNo(userNo)
        return ResponseEntity.ok(userResponse)
    }
}
