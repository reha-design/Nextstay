package com.mrmention.nextstay.domain.member.service

import com.mrmention.nextstay.domain.member.dto.AuthResponse
import com.mrmention.nextstay.domain.member.dto.LoginRequest
import com.mrmention.nextstay.domain.member.dto.LoginResult
import com.mrmention.nextstay.domain.member.dto.SignupRequest
import com.mrmention.nextstay.domain.member.entity.Member
import com.mrmention.nextstay.domain.member.entity.MemberRole
import com.mrmention.nextstay.global.exception.AlreadyExistsException
import com.mrmention.nextstay.global.exception.InvalidCredentialsException
import com.mrmention.nextstay.domain.member.repository.MemberRepository
import com.mrmention.nextstay.global.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicLong

@Service
@Transactional(readOnly = true)
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    // 임시 시퀀스 (운영 환경에서는 Redis나 DB 시퀀스 사용 권장)
    private val sequence = AtomicLong(1)

    /**
     * 회원 가입
     */
    @Transactional
    fun signup(request: SignupRequest): SignupResponse {
        if (memberRepository.findByEmail(request.email) != null) {
            throw AlreadyExistsException("이미 가입된 이메일입니다.")
        }

        if (!request.isPasswordMatching()) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
        }

        val userNo = generateUserNo(request.role)
        val encodedPassword = passwordEncoder.encode(request.password)

        val member = Member(
            userNo = userNo,
            email = request.email,
            password = encodedPassword,
            name = request.name,
            phone = request.phone,
            role = request.role,
            provider = request.provider,
            providerId = request.providerId,
            termsAgreed = request.termsAgreed,
            marketingAgreed = request.marketingAgreed
        )

        val savedMember = memberRepository.save(member)
        
        return SignupResponse(
            userNo = savedMember.userNo,
            email = savedMember.email,
            name = savedMember.name,
            role = savedMember.role
        )
    }

    /**
     * 로그인 및 JWT 발급
     */
    fun login(request: LoginRequest): LoginResult {
        val member = memberRepository.findByEmail(request.email)
            ?: throw InvalidCredentialsException("이메일 또는 비밀번호가 일치하지 않습니다.")

        if (!passwordEncoder.matches(request.password, member.password)) {
            throw InvalidCredentialsException("이메일 또는 비밀번호가 일치하지 않습니다.") }

        val token = jwtTokenProvider.createToken(member.userNo, member.role.name)
        val refreshToken = jwtTokenProvider.createRefreshToken(member.userNo, member.role.name)

        val authResponse = AuthResponse(
            accessToken = token,
            userNo = member.userNo,
            email = member.email,
            name = member.name,
            phone = member.phone,
            role = member.role
        )
        
        return LoginResult(authResponse, refreshToken)
    }

    /**
     * 리프레시 토큰 검증 및 액세스 토큰 재발급
     */
    fun refresh(refreshToken: String): LoginResult {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw InvalidCredentialsException("유효하지 않거나 만료된 리프레시 토큰입니다.")
        }

        val userNo = jwtTokenProvider.getUserNoFromToken(refreshToken)
        val member = memberRepository.findByUserNo(userNo)
            ?: throw InvalidCredentialsException("존재하지 않는 회원입니다.")

        val newAccessToken = jwtTokenProvider.createToken(member.userNo, member.role.name)
        val newRefreshToken = jwtTokenProvider.createRefreshToken(member.userNo, member.role.name)

        val authResponse = AuthResponse(
            accessToken = newAccessToken,
            userNo = member.userNo,
            email = member.email,
            name = member.name,
            phone = member.phone,
            role = member.role
        )

        return LoginResult(authResponse, newRefreshToken)
    }

    /**
     * 비즈니스 키 (user_no) 생성 로직
     * 예: m20260307-001
     */
    private fun generateUserNo(role: MemberRole): String {
        val prefix = if (role == MemberRole.HOST) "h" else "m"
        val date = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMddHHmm"))
        val random = (10..99).random()
        return "$prefix$date$random"
    }
}
