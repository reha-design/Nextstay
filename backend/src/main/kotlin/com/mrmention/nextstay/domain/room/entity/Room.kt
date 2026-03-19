package com.mrmention.nextstay.domain.room.entity

import com.mrmention.nextstay.domain.stay.entity.Stay
import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "rooms")
class Room(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @Column(name = "room_no", unique = true, nullable = false, columnDefinition = "BINARY(16)")
    val roomNo: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stay_id", nullable = false)
    val stay: Stay,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val pricePerNight: Int,

    @Column(nullable = false)
    val capacity: Int,

    @Column(columnDefinition = "TEXT")
    val description: String,

    @Column(name = "deleted_at")
    var deletedAt: java.time.LocalDateTime? = null
) : BaseEntity()
