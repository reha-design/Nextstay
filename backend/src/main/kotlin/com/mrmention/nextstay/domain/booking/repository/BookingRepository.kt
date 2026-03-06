package com.mrmention.nextstay.domain.booking.repository

import com.mrmention.nextstay.domain.booking.entity.Booking
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface BookingRepository : JpaRepository<Booking, Long> {
    fun findByBookingNo(bookingNo: String): Optional<Booking>
}
