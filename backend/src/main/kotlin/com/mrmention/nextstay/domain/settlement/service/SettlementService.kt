package com.mrmention.nextstay.domain.settlement.service

import com.mrmention.nextstay.domain.settlement.dto.SettlementResponse
import com.mrmention.nextstay.domain.settlement.entity.Settlement
import com.mrmention.nextstay.domain.settlement.repository.SettlementRepository
import com.mrmention.nextstay.domain.member.repository.MemberRepository
import com.mrmention.nextstay.global.exception.BusinessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class SettlementService(
    private val settlementRepository: SettlementRepository,
    private val memberRepository: MemberRepository
) {
    fun getMySettlements(userNo: String, pageable: Pageable): Page<SettlementResponse> {
        val member = memberRepository.findByUserNo(UUID.fromString(userNo))
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.")

        return settlementRepository.findByHostId(member.id, pageable)
            .map { it.toResponse() }
    }

    private fun Settlement.toResponse() = SettlementResponse(
        id = this.id.toString(),
        hostName = this.host.name, // nickname -> name
        settleMonth = this.settleMonth,
        totalAmount = this.totalAmount,
        feeAmount = this.feeAmount,
        netAmount = this.netAmount,
        status = this.status,
        createdAt = this.createdAt
    )
}
