package com.mrmention.nextstay.domain.cs.service

import com.mrmention.nextstay.domain.cs.dto.CsTicketRequest
import com.mrmention.nextstay.domain.cs.dto.CsTicketResponse
import com.mrmention.nextstay.domain.cs.entity.CsTicket
import com.mrmention.nextstay.domain.cs.entity.CsTicketStatus
import com.mrmention.nextstay.domain.cs.repository.CsTicketRepository
import com.mrmention.nextstay.domain.member.repository.MemberRepository
import com.mrmention.nextstay.domain.booking.repository.BookingRepository
import com.mrmention.nextstay.global.exception.BusinessException
import com.mrmention.nextstay.global.util.IdGenerator
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CsTicketService(
    private val csTicketRepository: CsTicketRepository,
    private val memberRepository: MemberRepository,
    private val bookingRepository: BookingRepository,
    private val idGenerator: IdGenerator
) {
    @Transactional
    fun createTicket(userNo: String, request: CsTicketRequest): String {
        val member = memberRepository.findByUserNo(UUID.fromString(userNo))
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.")

        val booking = request.bookingId?.let {
            val foundBooking = bookingRepository.findById(UUID.fromString(it)).orElseThrow {
                BusinessException(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다.")
            }

            if (foundBooking.guest.id != member.id) {
                throw BusinessException(HttpStatus.FORBIDDEN, "본인의 예약에 대해서만 문의할 수 있습니다.")
            }
            foundBooking
        }

        val ticket = CsTicket(
            id = idGenerator.generate(),
            member = member,
            booking = booking,
            subject = request.subject,
            content = request.content,
            priorityScore = request.priorityScore,
            status = CsTicketStatus.OPEN
        )

        return csTicketRepository.save(ticket).id.toString()
    }

    fun getMyTickets(userNo: String, pageable: Pageable): Page<CsTicketResponse> {
        val member = memberRepository.findByUserNo(UUID.fromString(userNo))
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.")

        return csTicketRepository.findByMemberId(member.id, pageable)
            .map { it.toResponse(member.name) }
    }

    private fun CsTicket.toResponse(userName: String) = CsTicketResponse(
        id = this.id.toString(),
        bookingId = this.booking?.id?.toString(),
        userName = userName,
        subject = this.subject,
        content = this.content,
        priorityScore = this.priorityScore,
        status = this.status,
        createdAt = this.createdAt
    )
}
