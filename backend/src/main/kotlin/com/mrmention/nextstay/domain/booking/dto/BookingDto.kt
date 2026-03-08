package com.mrmention.nextstay.domain.booking.dto

import com.mrmention.nextstay.domain.booking.entity.BookingStatus
import java.time.LocalDate

data class BookingCreateRequest(
    val roomNo: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate
)

data class BookingResponse(
    val bookingNo: String,
    val roomName: String,
    val stayName: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val totalPrice: Long,
    val status: BookingStatus,
    val guestName: String
)
