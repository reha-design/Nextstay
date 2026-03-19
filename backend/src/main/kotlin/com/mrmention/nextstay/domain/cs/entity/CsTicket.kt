package com.mrmention.nextstay.domain.cs.entity

import com.mrmention.nextstay.domain.booking.entity.Booking
import com.mrmention.nextstay.domain.member.entity.Member
import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*
import java.util.UUID

enum class CsTicketStatus { OPEN, RESOLVED }

@Entity
@Table(name = "cs_tickets")
class CsTicket(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    val booking: Booking? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Column(nullable = false)
    val subject: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    val content: String,

    @Column(name = "priority_score", nullable = false)
    val priorityScore: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: CsTicketStatus = CsTicketStatus.OPEN
) : BaseEntity()
