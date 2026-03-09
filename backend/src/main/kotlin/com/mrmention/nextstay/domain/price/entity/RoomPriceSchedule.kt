package com.mrmention.nextstay.domain.price.entity

import com.mrmention.nextstay.domain.room.entity.Room
import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "room_price_schedules")
class RoomPriceSchedule(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: Room,

    @Column(name = "start_date", nullable = false)
    val startDate: LocalDate,

    @Column(name = "end_date", nullable = false)
    val endDate: LocalDate,

    @Column(name = "base_price", nullable = false)
    val basePrice: Int,

    @Column(name = "peak_price")
    val peakPrice: Int? = null,

    @Column(name = "weekend_price")
    val weekendPrice: Int? = null,

    @Column(name = "period_name", length = 50)
    val periodName: String? = null
) : BaseEntity()
