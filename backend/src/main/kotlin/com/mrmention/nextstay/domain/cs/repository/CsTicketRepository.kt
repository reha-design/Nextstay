package com.mrmention.nextstay.domain.cs.repository

import com.mrmention.nextstay.domain.cs.entity.CsTicket
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CsTicketRepository : JpaRepository<CsTicket, UUID> {
    @EntityGraph(attributePaths = ["member", "booking"])
    override fun findAll(): List<CsTicket>

    @EntityGraph(attributePaths = ["member", "booking"])
    fun findByMemberId(memberId: UUID, pageable: Pageable): Page<CsTicket>
}
