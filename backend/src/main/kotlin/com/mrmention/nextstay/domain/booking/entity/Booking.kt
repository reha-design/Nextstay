package com.mrmention.nextstay.domain.booking.entity

import com.mrmention.nextstay.domain.member.entity.Member
import com.mrmention.nextstay.domain.room.entity.Room
import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

enum class BookingStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED }

@Entity
@Table(name = "bookings")
class Booking(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "booking_no", unique = true, nullable = false, length = 20)
    val bookingNo: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val guest: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: Room,

    @Column(nullable = false)
    val checkInDate: LocalDate,

    @Column(nullable = false)
    val checkOutDate: LocalDate,

    @Column(nullable = false)
    val totalPrice: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BookingStatus = BookingStatus.PENDING,

    @Column(name = "deleted_at")
    var deletedAt: java.time.LocalDateTime? = null
) : BaseEntity()
