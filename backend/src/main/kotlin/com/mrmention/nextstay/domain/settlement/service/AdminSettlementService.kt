package com.mrmention.nextstay.domain.settlement.service

import com.mrmention.nextstay.domain.settlement.dto.SettlementResponse
import com.mrmention.nextstay.domain.settlement.entity.Settlement
import com.mrmention.nextstay.domain.settlement.entity.SettlementStatus
import com.mrmention.nextstay.domain.settlement.repository.SettlementRepository
import com.mrmention.nextstay.global.exception.BusinessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class AdminSettlementService(
    private val settlementRepository: SettlementRepository
) {
    fun getAllSettlements(pageable: Pageable): Page<SettlementResponse> = settlementRepository.findAll(pageable).map { it.toResponse() }

    @Transactional
    fun updateSettlementStatus(id: UUID, status: SettlementStatus): SettlementResponse {
        val settlement = settlementRepository.findById(id).orElseThrow {
            BusinessException(HttpStatus.NOT_FOUND, "정산 내역을 찾을 수 없습니다.")
        }
        settlement.status = status
        return settlement.toResponse()
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
