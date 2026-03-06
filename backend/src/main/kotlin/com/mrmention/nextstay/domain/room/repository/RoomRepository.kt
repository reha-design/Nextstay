package com.mrmention.nextstay.domain.room.repository

import com.mrmention.nextstay.domain.room.entity.Room
import org.springframework.data.jpa.repository.JpaRepository

interface RoomRepository : JpaRepository<Room, Long>
