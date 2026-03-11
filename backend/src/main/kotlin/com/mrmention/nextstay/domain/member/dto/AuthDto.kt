package com.mrmention.nextstay.domain.member.dto

import com.mrmention.nextstay.domain.member.entity.MemberRole
import com.mrmention.nextstay.domain.member.entity.OnboardingStatus
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
    val phone: String?,
    val role: MemberRole,
    val onboardingStatus: OnboardingStatus
)

/**
 * 로그인 처리 결과 DTO (Controller로 전달용)
 * refreshToken은 응답 바디가 아닌 쿠키(HttpOnly)로 설정하기 위해 분리
 */
data class LoginResult(
    val authResponse: AuthResponse,
    val refreshToken: String
)
/**
 * 회원가입 결과 응답 DTO (자동 로그인 미사용 시)
 */
data class SignupResponse(
    val userNo: String,
    val email: String,
    val name: String,
    val role: MemberRole,
    val onboardingStatus: OnboardingStatus
)
/**
 * 현재 로그인된 사용자 정보 응답 DTO
 */
data class UserResponse(
    val userNo: String,
    val email: String,
    val name: String,
    val phone: String?,
    val role: MemberRole,
    val onboardingStatus: OnboardingStatus
)
