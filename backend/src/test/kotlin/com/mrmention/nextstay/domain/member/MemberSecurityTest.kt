package com.mrmention.nextstay.domain.member

import com.fasterxml.jackson.databind.ObjectMapper
import com.mrmention.nextstay.domain.member.controller.AuthController
import com.mrmention.nextstay.domain.member.dto.LoginRequest
import com.mrmention.nextstay.domain.member.dto.SignupRequest
import com.mrmention.nextstay.domain.member.entity.MemberRole
import com.mrmention.nextstay.domain.member.service.AuthService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc

@WebMvcTest(AuthController::class)
@AutoConfigureMockMvc(addFilters = false)
class MemberSecurityTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var authService: AuthService

    @Test
    @DisplayName("SQL 인젝션 페이로드가 포함된 회원가입 요청은 차단되어야 한다")
    fun signupWithSqlInjection() {
        val request = SignupRequest(
            role = MemberRole.GUEST,
            name = "admin' --",
            email = "test@example.com",
            password = "Password123!",
            passwordConfirm = "Password123!",
            phone = "010-1234-5678",
            termsAgreed = true
        )

        mockMvc.post("/api/v1/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @DisplayName("너무 긴 비밀번호(72자 초과)는 Bcrypt DoS 방지를 위해 차단되어야 한다")
    fun signupWithLongPassword() {
        val longPassword = "A".repeat(73)
        val request = SignupRequest(
            role = MemberRole.GUEST,
            name = "홍길동",
            email = "longpw@example.com",
            password = longPassword,
            passwordConfirm = longPassword,
            phone = "010-1234-5678",
            termsAgreed = true
        )

        mockMvc.post("/api/v1/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @DisplayName("비정상적인 전화번호 형식은 차단되어야 한다")
    fun signupWithInvalidPhone() {
        val request = SignupRequest(
            role = MemberRole.GUEST,
            name = "홍길동",
            email = "phone@example.com",
            password = "Password123!",
            passwordConfirm = "Password123!",
            phone = "010-ABCD-EFGH",
            termsAgreed = true
        )

        mockMvc.post("/api/v1/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @DisplayName("로그인 시에도 이메일 및 비밀번호 길이 제한이 작동해야 한다")
    fun loginWithLongPayload() {
        val longEmail = "A".repeat(256) + "@example.com"
        val request = LoginRequest(
            email = longEmail,
            password = "validPassword123!"
        )

        mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
