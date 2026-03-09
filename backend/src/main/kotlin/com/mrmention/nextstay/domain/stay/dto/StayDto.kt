package com.mrmention.nextstay.domain.stay.dto

import com.mrmention.nextstay.domain.stay.entity.StayCategory
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class StayRequest(
    @field:NotBlank(message = "숙소 이름은 필수입니다.")
    val name: String,
    
    val description: String,
    
    @field:NotBlank(message = "주소는 필수입니다.")
    val address: String,
    
    @field:NotBlank(message = "도시는 필수입니다.")
    val city: String,
    
    val category: StayCategory,
    
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    val discountPolicies: List<DiscountPolicyRequest> = emptyList(),
    val seasonPrices: List<SeasonPriceRequest> = emptyList()
)

data class DiscountPolicyRequest(
    val minNights: Int,
    val discountRate: java.math.BigDecimal
)

data class SeasonPriceRequest(
    val seasonName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val multiplier: java.math.BigDecimal
)

data class StayResponse(
    val stayNo: String,
    val name: String,
    val description: String,
    val address: String,
    val city: String,
    val category: StayCategory,
    val hostName: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val discountPolicies: List<DiscountPolicyResponse> = emptyList(),
    val seasonPrices: List<SeasonPriceResponse> = emptyList()
)

data class DiscountPolicyResponse(
    val minNights: Int,
    val discountRate: java.math.BigDecimal
)

data class SeasonPriceResponse(
    val seasonName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val multiplier: java.math.BigDecimal
)

data class MainPageStayResponse(
    val stayNo: String,
    val name: String,
    val address: String,
    val category: String,
    val minPrice: Int,
    val thumbnailUrl: String,
    val rating: Double,
    val priceTiers: List<PriceTierDto> = emptyList()
)

data class PriceTierDto(
    val nights: Int,
    val price: Long,
    val originalPrice: Long,
    val discountRate: Int
)

data class StayDetailResponse(
    val stayNo: String,
    val name: String,
    val description: String,
    val address: String,
    val city: String,
    val category: String,
    val hostName: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val rating: Double,
    val images: List<String>,
    val rooms: List<RoomDetailResponse>
)

data class RoomDetailResponse(
    val roomNo: String,
    val name: String,
    val description: String,
    val type: String,
    val basePrice: Int,
    val capacity: Int,
    val imageUrls: List<String>,
    val monthlyPrice: Long? = null,
    val discountRate: Int? = null,
    val badgeText: String? = null
)
