package com.mrmention.nextstay.domain.room.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class RoomRequest(
    @field:NotBlank(message = "숙소 식별번호는 필수입니다.")
    val stayNo: String,
    
    @field:NotBlank(message = "객실 이름은 필수입니다.")
    val name: String,
    
    @field:Min(value = 0, message = "1박 가격은 0 이상이어야 합니다.")
    val pricePerNight: Int,
    
    @field:Min(value = 1, message = "수용 인원은 1명 이상이어야 합니다.")
    val capacity: Int,
    
    val description: String
)

data class RoomResponse(
    val roomNo: String,
    val stayName: String,
    val name: String,
    val pricePerNight: Int,
    val capacity: Int,
    val description: String
)
