package com.mrmention.nextstay.domain.host.dto

import com.mrmention.nextstay.domain.host.entity.HostApplicationStatus
import java.time.LocalDateTime

data class HostApplicationRequest(
    val stayName: String,
    val businessNo: String,
    val documentUrls: List<String>
)

data class HostApplicationResponse(
    val id: String,
    val stayName: String,
    val businessNo: String,
    val status: HostApplicationStatus,
    val createdAt: LocalDateTime
)
