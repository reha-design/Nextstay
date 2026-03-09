package com.mrmention.nextstay.domain.price.repository

import com.mrmention.nextstay.domain.price.entity.RoomPriceSchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface RoomPriceScheduleRepository : JpaRepository<RoomPriceSchedule, Long> {
    @Query("SELECT rps FROM RoomPriceSchedule rps WHERE rps.room.id = :roomId AND rps.startDate <= :endDate AND rps.endDate >= :startDate")
    fun findByRoomIdAndDateRange(roomId: Long, startDate: LocalDate, endDate: LocalDate): List<RoomPriceSchedule>
}
