package com.mrmention.nextstay.domain.room.service

import com.mrmention.nextstay.domain.room.dto.RoomRequest
import com.mrmention.nextstay.domain.room.dto.RoomResponse
import com.mrmention.nextstay.domain.room.entity.Room
import com.mrmention.nextstay.domain.room.repository.RoomRepository
import com.mrmention.nextstay.domain.stay.repository.StayRepository
import com.mrmention.nextstay.global.exception.BusinessException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicLong

@Service
@Transactional(readOnly = true)
class RoomService(
    private val roomRepository: RoomRepository,
    private val stayRepository: StayRepository
) {
    private val sequence = AtomicLong(1)

    @Transactional
    fun createRoom(request: RoomRequest, userNo: String): String {
        val stay = stayRepository.findByStayNo(request.stayNo)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "숙소 정보를 찾을 수 없습니다.")

        // 권한 확인: 숙소를 등록한 호스트만 객실을 추가할 수 있음
        if (stay.host.userNo != userNo) {
            throw BusinessException(HttpStatus.FORBIDDEN, "객실을 등록할 권한이 없습니다.")
        }

        val roomNo = generateRoomNo()
        val room = Room(
            roomNo = roomNo,
            stay = stay,
            name = request.name,
            pricePerNight = request.pricePerNight,
            capacity = request.capacity,
            description = request.description
        )

        val savedRoom = roomRepository.save(room)
        return savedRoom.roomNo
    }

    fun getRoomsByStay(stayId: Long): List<RoomResponse> {
        return roomRepository.findAllByStayId(stayId).map { it.toResponse() }
    }

    fun getRoomDetail(roomNo: String): RoomResponse {
        val room = roomRepository.findByRoomNo(roomNo)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "객실 정보를 찾을 수 없습니다.")
        return room.toResponse()
    }

    private fun generateRoomNo(): String {
        val date = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMddHHmm"))
        val random = (10..99).random()
        return "r$date$random"
    }

    private fun Room.toResponse() = RoomResponse(
        roomNo = this.roomNo,
        stayName = this.stay.name,
        name = this.name,
        pricePerNight = this.pricePerNight,
        capacity = this.capacity,
        description = this.description
    )
}
