package com.mrmention.nextstay.domain.member.dto

import com.mrmention.nextstay.domain.member.entity.MemberRole
import com.mrmention.nextstay.domain.member.entity.SocialProvider
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.AssertTrue

/**
 * 회원가입 요청 DTO
 * - 게스트/호스트 구분, 이름, 이메일, 비밀번호 확인, 전화번호, 약관 동의 포함
 */
data class SignupRequest(
    /** 계정 구분: GUEST(게스트) or HOST(호스트) */
    val role: MemberRole = MemberRole.GUEST,

    @field:NotBlank(message = "이름은 필수 입력 항목입니다.")
    val name: String,

    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}$",
        message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    val password: String,

    /** 비밀번호 확인 */
    @field:NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
    val passwordConfirm: String,

    @field:NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    val phone: String,

    /** 전체 약관 동의 여부 */
    @field:AssertTrue(message = "약관에 동의해야 합니다.")
    val termsAgreed: Boolean = false,

    /** 마케팅 정보 수신 동의 (선택) */
    val marketingAgreed: Boolean = false,

    // 소셜 로그인 전용 필드 (일반 가입 시 사용 안 함)
    val provider: SocialProvider = SocialProvider.LOCAL,
    val providerId: String? = null
) {
    /** 비밀번호 일치 여부 확인 */
    fun isPasswordMatching() = password == passwordConfirm

    /** 호스트 가입 시 필수 항목 검증 */
    @AssertTrue(message = "호스트로 가입하려면 이름과 전화번호가 올바르게 입력되어야 합니다.")
    fun isValidHostRequest(): Boolean {
        if (role == MemberRole.HOST) {
            return name.isNotBlank() && phone.isNotBlank()
        }
        return true
    }
}
