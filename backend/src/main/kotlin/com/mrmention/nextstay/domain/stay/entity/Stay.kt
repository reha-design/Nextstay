package com.mrmention.nextstay.domain.stay.entity

import com.mrmention.nextstay.domain.member.entity.Member
import com.mrmention.nextstay.domain.room.entity.Room
import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*

enum class StayCategory { HOTEL, PENSION, APARTMENT, GUESTHOUSE }

@Entity
@Table(name = "stays")
class Stay(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "stay_no", unique = true, nullable = false, length = 20)
    val stayNo: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    val host: Member,

    @Column(nullable = false)
    val name: String,

    @Column(columnDefinition = "TEXT")
    val description: String,

    @Column(nullable = false)
    val address: String,

    @Column(nullable = false)
    val city: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: StayCategory,

    @Column(name = "deleted_at")
    var deletedAt: java.time.LocalDateTime? = null,

    @OneToMany(mappedBy = "stay", cascade = [CascadeType.ALL], orphanRemoval = true)
    val rooms: MutableList<Room> = mutableListOf()
) : BaseEntity()
