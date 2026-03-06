package com.mrmention.nextstay.domain.member.repository

import com.mrmention.nextstay.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByEmail(email: String): Optional<Member>
}
