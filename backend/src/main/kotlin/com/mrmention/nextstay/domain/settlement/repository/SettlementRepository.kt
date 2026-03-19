package com.mrmention.nextstay.domain.settlement.repository

import com.mrmention.nextstay.domain.settlement.entity.Settlement
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SettlementRepository : JpaRepository<Settlement, UUID> {
    fun findByHostId(hostId: UUID, pageable: Pageable): Page<Settlement>
}
