package com.mrmention.nextstay.domain.stay.entity

import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "stay_season_prices")
class StaySeasonPrice(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stay_id", nullable = false)
    val stay: Stay,

    @Column(name = "season_name", nullable = false)
    val seasonName: String,

    @Column(name = "start_date", nullable = false)
    val startDate: LocalDate,

    @Column(name = "end_date", nullable = false)
    val endDate: LocalDate,

    @Column(name = "multiplier", nullable = false, precision = 5, scale = 3)
    val multiplier: java.math.BigDecimal // 예: 1.5 (50% 인상), 1.0 (기본)
) : BaseEntity()
