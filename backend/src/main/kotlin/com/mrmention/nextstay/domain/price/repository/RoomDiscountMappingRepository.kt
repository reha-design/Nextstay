package com.mrmention.nextstay.domain.price.repository

import com.mrmention.nextstay.domain.price.entity.RoomDiscountMapping
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RoomDiscountMappingRepository : JpaRepository<RoomDiscountMapping, Long> {
    @Query("SELECT rdm FROM RoomDiscountMapping rdm JOIN FETCH rdm.discountPolicy WHERE rdm.room.id = :roomId AND rdm.isActive = true")
    fun findActiveByRoomId(roomId: Long): List<RoomDiscountMapping>
}
