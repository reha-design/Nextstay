package com.mrmention.nextstay.domain.member.entity

import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*

enum class MemberRole { GUEST, HOST, ADMIN }

@Entity
@Table(name = "members")
class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

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
    val role: MemberRole
) : BaseEntity()
