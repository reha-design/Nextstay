package com.mrmention.nextstay.domain.member.entity

import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*
import java.util.UUID

enum class MemberRole { GUEST, HOST, ADMIN }
enum class SocialProvider { LOCAL, KAKAO, APPLE }
enum class OnboardingStatus { NONE, PENDING, COMPLETED, APPROVED }

@Entity
@Table(
    name = "members",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_member_email", columnNames = ["email"]),
        UniqueConstraint(name = "uk_member_user_no", columnNames = ["user_no"])
    ]
)
class Member(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @Column(name = "user_no", unique = true, nullable = false, columnDefinition = "BINARY(16)")
    val userNo: UUID,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val phone: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: MemberRole,

    @Enumerated(EnumType.STRING)
    @Column(name = "onboarding_status", nullable = false)
    var onboardingStatus: OnboardingStatus = OnboardingStatus.NONE,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val provider: SocialProvider = SocialProvider.LOCAL,

    @Column(name = "provider_id")
    val providerId: String? = null,

    @Column(name = "terms_agreed", nullable = false)
    val termsAgreed: Boolean = false,

    @Column(name = "marketing_agreed", nullable = false)
    val marketingAgreed: Boolean = false,

    @Column(name = "deleted_at")
    var deletedAt: java.time.LocalDateTime? = null
) : BaseEntity()
