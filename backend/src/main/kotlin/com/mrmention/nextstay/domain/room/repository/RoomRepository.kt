package com.mrmention.nextstay.domain.room.repository

import com.mrmention.nextstay.domain.room.entity.Room
import org.springframework.data.jpa.repository.JpaRepository


import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RoomRepository : JpaRepository<Room, Long> {
    fun findByRoomNo(roomNo: String): Room?
    
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    fun findWithLockById(@Param("id") id: Long): Room?
    
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.roomNo = :roomNo")
    fun findWithLockByRoomNo(@Param("roomNo") roomNo: String): Room?
    
    @org.springframework.data.jpa.repository.Query("SELECT r FROM Room r " +
           "JOIN FETCH r.stay s " +
           "LEFT JOIN FETCH s.seasonPrices " +
           "LEFT JOIN FETCH s.discountPolicies " +
           "WHERE r.roomNo = :roomNo")
    fun findRoomWithPricingData(@Param("roomNo") roomNo: String): Room?
    fun findAllByStayId(stayId: Long): List<Room>
}
