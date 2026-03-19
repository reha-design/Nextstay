package com.mrmention.nextstay.domain.host.repository

import com.mrmention.nextstay.domain.host.entity.HostApplication
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface HostApplicationRepository : JpaRepository<HostApplication, UUID> {
    fun findByMemberId(memberId: UUID): HostApplication?
}
