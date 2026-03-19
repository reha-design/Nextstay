package com.mrmention.nextstay.domain.price.repository

import com.mrmention.nextstay.domain.price.entity.RoomPriceSchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.UUID

interface RoomPriceScheduleRepository : JpaRepository<RoomPriceSchedule, UUID> {
    fun findAllByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        roomId: UUID,
        endDate: LocalDate,
        startDate: LocalDate
    ): List<RoomPriceSchedule>
}
