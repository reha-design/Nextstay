package com.mrmention.nextstay.domain.settlement.entity

import com.mrmention.nextstay.domain.member.entity.Member
import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*
import java.util.UUID

enum class SettlementStatus { PENDING, COMPLETED }

@Entity
@Table(name = "settlements")
class Settlement(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    val host: Member,

    @Column(name = "settle_month", nullable = false, length = 7) // YYYY-MM
    val settleMonth: String,

    @Column(name = "total_amount", nullable = false)
    val totalAmount: Long,

    @Column(name = "fee_amount", nullable = false)
    val feeAmount: Long,

    @Column(name = "net_amount", nullable = false)
    val netAmount: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SettlementStatus = SettlementStatus.PENDING
) : BaseEntity()
