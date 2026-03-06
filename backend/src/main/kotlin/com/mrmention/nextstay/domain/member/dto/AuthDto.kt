package com.mrmention.nextstay.domain.member.dto

import com.mrmention.nextstay.domain.member.entity.MemberRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * 일반 이메일 로그인 요청 DTO
 */
data class LoginRequest(
    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    val password: String
)

/**
 * 인증 성공 응답 DTO
 */
data class AuthResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val userNo: String,
    val email: String,
    val name: String,
    val role: MemberRole
)
