package com.mrmention.nextstay.domain.stay.dto

import com.mrmention.nextstay.domain.stay.entity.StayCategory
import jakarta.validation.constraints.NotBlank

data class StayRequest(
    @field:NotBlank(message = "숙소 이름은 필수입니다.")
    val name: String,
    
    val description: String,
    
    @field:NotBlank(message = "주소는 필수입니다.")
    val address: String,
    
    @field:NotBlank(message = "도시는 필수입니다.")
    val city: String,
    
    val category: StayCategory
)

data class StayResponse(
    val stayNo: String,
    val name: String,
    val description: String,
    val address: String,
    val city: String,
    val category: StayCategory,
    val hostName: String
)
