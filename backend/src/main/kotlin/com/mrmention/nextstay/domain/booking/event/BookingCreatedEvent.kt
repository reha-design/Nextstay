package com.mrmention.nextstay.domain.booking.event

import com.mrmention.nextstay.domain.booking.entity.Booking

import java.util.UUID

data class BookingCreatedEvent(
    val bookingNo: UUID,
    val userNo: UUID,
    val roomNo: UUID,
    val guestName: String
) {
    companion object {
        fun from(booking: Booking) = BookingCreatedEvent(
            bookingNo = booking.bookingNo,
            userNo = booking.guest.userNo,
            roomNo = booking.room.roomNo,
            guestName = booking.guest.name
        )
    }
}
