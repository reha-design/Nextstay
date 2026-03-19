package com.mrmention.nextstay.domain.cs.service

import com.mrmention.nextstay.domain.cs.dto.CsTicketResponse
import com.mrmention.nextstay.domain.cs.entity.CsTicket
import com.mrmention.nextstay.domain.cs.entity.CsTicketStatus
import com.mrmention.nextstay.domain.cs.repository.CsTicketRepository
import com.mrmention.nextstay.global.exception.BusinessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class AdminCsService(
    private val csTicketRepository: CsTicketRepository
) {
    fun getAllTickets(pageable: Pageable): Page<CsTicketResponse> = csTicketRepository.findAll(pageable).map { it.toResponse() }

    @Transactional
    fun updateTicketStatus(id: UUID, status: CsTicketStatus): CsTicketResponse {
        val ticket = csTicketRepository.findById(id).orElseThrow {
            BusinessException(HttpStatus.NOT_FOUND, "민원 티켓을 찾을 수 없습니다.")
        }
        ticket.status = status
        return ticket.toResponse()
    }

    private fun CsTicket.toResponse() = CsTicketResponse(
        id = this.id.toString(),
        bookingId = this.booking?.id?.toString(),
        userName = this.member.name, // nickname -> name
        subject = this.subject,
        content = this.content,
        priorityScore = this.priorityScore,
        status = this.status,
        createdAt = this.createdAt
    )
}
