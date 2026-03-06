package com.mrmention.nextstay.domain.booking.repository

import com.mrmention.nextstay.domain.booking.entity.Booking
import org.springframework.data.jpa.repository.JpaRepository

interface BookingRepository : JpaRepository<Booking, Long>
