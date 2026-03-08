package com.mrmention.nextstay.domain.room.controller

import com.mrmention.nextstay.domain.room.dto.RoomRequest
import com.mrmention.nextstay.domain.room.dto.RoomResponse
import com.mrmention.nextstay.domain.room.service.RoomService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@Tag(name = "Room", description = "객실 관리 API")
@RestController
@RequestMapping("/api/v1/rooms")
class RoomController(
    private val roomService: RoomService
) {

    @Operation(summary = "신규 객실 등록", description = "호스트가 소유한 숙소에 객실을 추가합니다.")
    @PostMapping
    fun createRoom(
        @Valid @RequestBody request: RoomRequest,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<String> {
        val roomNo = roomService.createRoom(request, user.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(roomNo)
    }

    @Operation(summary = "숙소별 객실 목록 조회", description = "특정 숙소에 속한 객실 목록을 조회합니다.")
    @GetMapping("/stay/{stayId}")
    fun getRoomsByStay(@PathVariable stayId: Long): ResponseEntity<List<RoomResponse>> {
        val response = roomService.getRoomsByStay(stayId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "객실 상세 조회", description = "객실 식별 번호(roomNo)를 기반으로 상세 정보를 조회합니다.")
    @GetMapping("/{roomNo}")
    fun getRoomDetail(@PathVariable roomNo: String): ResponseEntity<RoomResponse> {
        val response = roomService.getRoomDetail(roomNo)
        return ResponseEntity.ok(response)
    }
}
