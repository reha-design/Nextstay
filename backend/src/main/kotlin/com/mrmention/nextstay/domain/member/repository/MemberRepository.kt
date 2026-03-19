package com.mrmention.nextstay.domain.member.repository

import com.mrmention.nextstay.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID


interface MemberRepository : JpaRepository<Member, UUID> {
    fun findByEmail(email: String): Member?
    fun findByUserNo(userNo: UUID): Member?
}
