package com.mrmention.nextstay.domain.booking.repository

import com.mrmention.nextstay.domain.booking.entity.Booking
import com.mrmention.nextstay.domain.booking.entity.BookingStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate


interface BookingRepository : JpaRepository<Booking, Long> {
    fun findByBookingNo(bookingNo: String): Booking?
    
    fun findAllByGuestId(guestId: Long): List<Booking>

    /**
     * 특정 기간 동안 특정 객실에 대해 이미 확정(CONFIRMED)되거나 대기(PENDING) 중인 예약이 있는지 확인
     * (체크인 <= 기존체크아웃) AND (체크아웃 >= 기존체크인) 조건으로 겹치는 구간 검색
     */
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.room.id = :roomId 
        AND b.status IN :statuses
        AND b.checkInDate < :checkOutDate 
        AND b.checkOutDate > :checkInDate
    """)
    fun findOverlappingBookings(
        @Param("roomId") roomId: Long,
        @Param("checkInDate") checkInDate: LocalDate,
        @Param("checkOutDate") checkOutDate: LocalDate,
        @Param("statuses") statuses: List<BookingStatus>
    ): List<Booking>
}
