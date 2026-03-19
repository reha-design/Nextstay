package com.mrmention.nextstay.domain.cs.dto

import com.mrmention.nextstay.domain.cs.entity.CsTicketStatus
import java.time.LocalDateTime

data class CsTicketRequest(
    val bookingId: String? = null,
    val subject: String,
    val content: String,
    val priorityScore: Int = 0
)

data class CsTicketResponse(
    val id: String,
    val bookingId: String? = null,
    val userName: String,
    val subject: String,
    val content: String,
    val priorityScore: Int,
    val status: CsTicketStatus,
    val createdAt: LocalDateTime
)
