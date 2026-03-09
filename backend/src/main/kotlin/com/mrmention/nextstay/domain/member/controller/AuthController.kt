package com.mrmention.nextstay.domain.member.controller

import com.mrmention.nextstay.domain.member.dto.AuthResponse
import com.mrmention.nextstay.domain.member.dto.LoginRequest
import com.mrmention.nextstay.domain.member.dto.LoginResult
import com.mrmention.nextstay.domain.member.dto.SignupResponse
import com.mrmention.nextstay.domain.member.dto.SignupRequest
import com.mrmention.nextstay.domain.member.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Authentication", description = "회원가입 및 로그인 API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @Operation(summary = "일반 이메일 회원가입", description = "이메일을 사용하여 새로운 계정을 생성합니다.")
    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody request: SignupRequest,
        response: HttpServletResponse
    ): ResponseEntity<SignupResponse> {
        val result = authService.signup(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }

    @Operation(summary = "일반 이메일 로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받고 HttpOnly 쿠키로 Refresh Token을 반환합니다.")
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponse> {
        val result = authService.login(request)
        
        // Refresh Token을 HttpOnly 쿠키로 설정
        val refreshCookie = ResponseCookie.from("refresh_token", result.refreshToken)
            .httpOnly(true)
            .secure(false) // 로컬 테스트용
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Lax")
            .build()
            
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())

        return ResponseEntity.ok(result.authResponse)
    }

    @Operation(summary = "액세스 토큰 갱신", description = "HttpOnly 쿠키의 Refresh Token을 검증하여 새로운 액세스 토큰을 발급합니다.")
    @PostMapping("/refresh")
    fun refresh(
        @CookieValue(name = "refresh_token", required = false) refreshToken: String?,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponse> {
        if (refreshToken.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        
        // Refresh Service 로직 호출
        val result = authService.refresh(refreshToken)
        
        // 새로운 Refresh Token도 발급되었다면 쿠키 갱신(RTR 패턴)
        val refreshCookie = ResponseCookie.from("refresh_token", result.refreshToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Lax")
            .build()
            
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())

        return ResponseEntity.ok(result.authResponse)
    }
}
