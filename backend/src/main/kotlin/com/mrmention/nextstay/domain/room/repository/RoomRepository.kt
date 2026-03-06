package com.mrmention.nextstay.domain.room.repository

import com.mrmention.nextstay.domain.room.entity.Room
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RoomRepository : JpaRepository<Room, Long> {
    fun findByRoomNo(roomNo: String): Optional<Room>
}
