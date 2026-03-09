package com.mrmention.nextstay.domain.price.entity

import com.mrmention.nextstay.domain.room.entity.Room
import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "room_discount_mappings")
class RoomDiscountMapping(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: Room,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    val discountPolicy: DiscountPolicy,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseEntity()
