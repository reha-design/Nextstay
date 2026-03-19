package com.mrmention.nextstay.domain.booking.service

import com.mrmention.nextstay.domain.booking.dto.BookingCreateRequest
import com.mrmention.nextstay.domain.booking.dto.BookingResponse
import com.mrmention.nextstay.domain.booking.entity.Booking
import com.mrmention.nextstay.domain.booking.entity.BookingStatus
import com.mrmention.nextstay.domain.booking.repository.BookingRepository
import com.mrmention.nextstay.domain.member.repository.MemberRepository
import com.mrmention.nextstay.domain.price.dto.PriceCalculationRequest
import com.mrmention.nextstay.domain.price.service.PriceService
import com.mrmention.nextstay.domain.room.repository.RoomRepository
import com.mrmention.nextstay.global.exception.BusinessException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.context.ApplicationEventPublisher
import com.mrmention.nextstay.domain.booking.event.BookingCreatedEvent
import com.mrmention.nextstay.global.util.IdGenerator
import com.mrmention.nextstay.global.util.TimeProvider
import java.util.UUID

@Service
@Transactional(readOnly = true)
class BookingService(
    private val bookingRepository: BookingRepository,
    private val roomRepository: RoomRepository,
    private val memberRepository: MemberRepository,
    private val priceService: PriceService,
    private val eventPublisher: ApplicationEventPublisher,
    private val timeProvider: TimeProvider,
    private val idGenerator: IdGenerator
) {

    /**
     * 예약 생성 (동시성 제어 적용: Pessimistic Lock)
     * MySQL 기본인 REPEATABLE_READ에서는 락 획득 전의 스냅샷을 볼 수 있으므로 READ_COMMITTED 권장
     */
    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    fun createBooking(request: BookingCreateRequest, userNo: String): String {
        // 0. 객실 조회 및 비관적 락(Write Lock) 획득 - 트랜잭션 시작 직후 최우선 수행
        val tempRoom = roomRepository.findByRoomNo(UUID.fromString(request.roomNo))
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "객실 정보를 찾을 수 없습니다.")

        val room = roomRepository.findWithLockById(tempRoom.id)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "객실 정보를 찾을 수 없습니다.")

        // 1. 해당 유저 정보 조회
        val guest = memberRepository.findByUserNo(UUID.fromString(userNo))
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.")

        // 3. 겹치는 예약이 있는지 최종 검증
        val overlapping = bookingRepository.findOverlappingBookings(
            roomId = room.id,
            checkInDate = request.checkInDate,
            checkOutDate = request.checkOutDate,
            statuses = listOf(BookingStatus.CONFIRMED, BookingStatus.PENDING)
        )

        if (overlapping.isNotEmpty()) {
            throw BusinessException(HttpStatus.CONFLICT, "해당 기간에 이미 예약이 존재합니다.")
        }

        // 4. 가격 재계산 및 검증 (서버 측 최종 계산)
        val priceInfo = priceService.calculatePrice(
            roomNo = room.roomNo,
            request = PriceCalculationRequest(request.checkInDate, request.checkOutDate)
        )

        // 5. 예약 데이터 생성
        val bookingId = idGenerator.generate()
        val bookingNo = idGenerator.generate()
        val booking = Booking(
            id = bookingId,
            bookingNo = bookingNo,
            guest = guest,
            room = room,
            checkInDate = request.checkInDate,
            checkOutDate = request.checkOutDate,
            totalPrice = priceInfo.pricing.finalTotalPrice, // Booking 엔티티의 totalPrice가 Long으로 변경됨
            status = BookingStatus.CONFIRMED // 이번 시뮬레이션에서는 즉시 확정으로 처리
        )

        val saved = bookingRepository.saveAndFlush(booking)

        // 6. 비동기 메시지 발행을 위한 서버 이벤트 발행
        // @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)를 통해 트랜잭션 성공 시에만 전송됨
        eventPublisher.publishEvent(BookingCreatedEvent.from(saved))

        return saved.bookingNo.toString()
    }

    /**
     * 내 예약 목록 조회
     */
    fun getMyBookings(userNo: String): List<BookingResponse> {
        val member = memberRepository.findByUserNo(UUID.fromString(userNo))
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.")

        return bookingRepository.findAllByGuestId(member.id).map { it.toResponse() }
    }

    private fun Booking.toResponse() = BookingResponse(
        bookingNo = this.bookingNo.toString(),
        roomName = this.room.name,
        stayName = this.room.stay.name,
        checkInDate = this.checkInDate,
        checkOutDate = this.checkOutDate,
        totalPrice = this.totalPrice,
        status = this.status,
        guestName = this.guest.name
    )
}
