package com.mrmention.nextstay.domain.booking.controller

import com.mrmention.nextstay.domain.booking.dto.BookingCreateRequest
import com.mrmention.nextstay.domain.booking.dto.BookingResponse
import com.mrmention.nextstay.domain.booking.service.BookingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@Tag(name = "Booking", description = "예약 관리 API")
@RestController
@RequestMapping("/api/v1")
class BookingController(
    private val bookingService: BookingService
) {

    @Operation(summary = "객실 예약 생성", description = "특정 객실에 대해 예약을 생성합니다. (GUEST 권한 필요)")
    @PostMapping("/rooms/{roomNo}/bookings")
    fun createBooking(
        @PathVariable roomNo: String,
        @RequestBody request: BookingCreateRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<String> {
        val bookingNo = bookingService.createBooking(request, userDetails.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingNo)
    }

    @Operation(summary = "내 예약 목록 조회", description = "로그인한 사용자의 모든 예약 내역을 조회합니다.")
    @GetMapping("/members/me/bookings")
    fun getMyBookings(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<List<BookingResponse>> {
        val responses = bookingService.getMyBookings(userDetails.username)
        return ResponseEntity.ok(responses)
    }
}
