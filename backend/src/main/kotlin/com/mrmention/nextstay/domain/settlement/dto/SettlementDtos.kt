package com.mrmention.nextstay.domain.settlement.dto

import com.mrmention.nextstay.domain.settlement.entity.SettlementStatus
import java.time.LocalDateTime

data class SettlementResponse(
    val id: String,
    val hostName: String,
    val settleMonth: String,
    val totalAmount: Long,
    val feeAmount: Long,
    val netAmount: Long,
    val status: SettlementStatus,
    val createdAt: LocalDateTime
)
